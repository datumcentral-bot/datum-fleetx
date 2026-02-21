package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Customer;
import com.datum.fleetx.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    
    List<Invoice> findByCompany(Company company);
    
    List<Invoice> findByCompanyId(UUID companyId);
    
    Page<Invoice> findByCompanyId(UUID companyId, Pageable pageable);
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByCustomer(Customer customer);
    
    List<Invoice> findByCompanyAndStatus(Company company, Invoice.InvoiceStatus status);
    
    List<Invoice> findByCompanyIdAndStatus(UUID companyId, Invoice.InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.dueDate < :date AND i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findOverdueInvoices(UUID companyId, LocalDate date);
    
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.dueDate < CURRENT_DATE AND i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findOverdueInvoices(UUID companyId);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.company.id = :companyId AND i.status = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumPaidByDateRange(UUID companyId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.company.id = :companyId AND i.status NOT IN ('PAID', 'CANCELLED')")
    java.math.BigDecimal sumOutstanding(UUID companyId);
}
