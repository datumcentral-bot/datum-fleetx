package com.datum.fleetx.service;

import com.datum.fleetx.entity.*;
import com.datum.fleetx.exception.ResourceNotFoundException;
import com.datum.fleetx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Invoice> getAllInvoicesByCompany(UUID companyId) {
        return invoiceRepository.findByCompanyId(companyId);
    }

    public Invoice getInvoiceById(UUID id, UUID companyId) {
        return invoiceRepository.findById(id)
            .filter(invoice -> invoice.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    public Invoice createInvoice(UUID customerId, BigDecimal amount, String description, UUID companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
            
        Customer customer = customerRepository.findById(customerId)
            .filter(c -> c.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber(companyId);

        // Calculate tax
        BigDecimal taxRate = new BigDecimal("0.08"); // 8% default
        BigDecimal taxAmount = amount.multiply(taxRate);
        BigDecimal totalAmount = amount.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setSubtotal(amount);
        invoice.setTaxRate(taxRate);
        invoice.setTaxAmount(taxAmount);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(totalAmount);
        invoice.setCurrency(company.getBaseCurrency() != null ? company.getBaseCurrency() : "USD");
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setNotes(description);

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoiceStatus(UUID id, Invoice.InvoiceStatus status, UUID companyId) {
        Invoice invoice = getInvoiceById(id, companyId);
        invoice.setStatus(status);
        
        if (status == Invoice.InvoiceStatus.PAID) {
            invoice.setPaidAt(LocalDate.now());
            invoice.setPaidAmount(invoice.getTotalAmount());
        }
        
        return invoiceRepository.save(invoice);
    }

    public Invoice recordPayment(UUID id, BigDecimal amount, UUID companyId) {
        Invoice invoice = getInvoiceById(id, companyId);
        
        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        invoice.setPaidAmount(currentPaid.add(amount));
        
        if (invoice.getPaidAmount().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDate.now());
        } else if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIAL_PAID);
        }
        
        return invoiceRepository.save(invoice);
    }

    public Invoice sendInvoice(UUID id, UUID companyId) {
        Invoice invoice = getInvoiceById(id, companyId);
        invoice.setStatus(Invoice.InvoiceStatus.SENT);
        invoice.setSentAt(LocalDate.now());
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(UUID id, UUID companyId) {
        Invoice invoice = getInvoiceById(id, companyId);
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Cannot delete a paid invoice");
        }
        invoice.setActive(false);
        invoiceRepository.save(invoice);
    }

    public List<Invoice> getOverdueInvoices(UUID companyId) {
        return invoiceRepository.findOverdueInvoices(companyId);
    }

    public List<Invoice> getPendingInvoices(UUID companyId) {
        return invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.SENT);
    }

    // Statistics
    public BigDecimal getTotalRevenue(UUID companyId) {
        List<Invoice> paidInvoices = invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.PAID);
        return paidInvoices.stream()
            .map(Invoice::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPending(UUID companyId) {
        BigDecimal outstanding = invoiceRepository.sumOutstanding(companyId);
        return outstanding != null ? outstanding : BigDecimal.ZERO;
    }

    public BigDecimal getTotalOverdue(UUID companyId) {
        List<Invoice> overdueInvoices = getOverdueInvoices(companyId);
        return overdueInvoices.stream()
            .map(Invoice::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateInvoiceNumber(UUID companyId) {
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%s-%04d", 
            LocalDate.now().getYear(), count);
    }
}
