import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Plus, Search, DollarSign, FileText, CheckCircle, Clock, AlertTriangle, Download, Send, MoreVertical } from 'lucide-react'
import api from '../services/api'

// Fetch invoices from API
const fetchInvoices = async () => {
  try {
    const response = await api.get('/invoices')
    return response.data.data || []
  } catch (error) {
    console.error('Failed to fetch invoices:', error)
    return []
  }
}

// Mock data for demo/offline mode
const mockInvoices = [
  { id: 'INV-001', invoiceNumber: 'INV-2026-001', customer: 'ABC Logistics', amount: 2500, tax: 187.50, total: 2687.50, status: 'PAID', issueDate: '2026-02-15', dueDate: '2026-03-15', paidDate: '2026-02-20' },
  { id: 'INV-002', invoiceNumber: 'INV-2026-002', customer: 'XYZ Freight', amount: 1200, tax: 90.00, total: 1290.00, status: 'PENDING', issueDate: '2026-02-18', dueDate: '2026-03-18', paidDate: null },
  { id: 'INV-003', invoiceNumber: 'INV-2026-003', customer: 'Global Cargo', amount: 3100, tax: 232.50, total: 3332.50, status: 'OVERDUE', issueDate: '2026-01-20', dueDate: '2026-02-20', paidDate: null },
  { id: 'INV-004', invoiceNumber: 'INV-2026-004', customer: 'Metro Shipping', amount: 1800, tax: 135.00, total: 1935.00, status: 'PENDING', issueDate: '2026-02-19', dueDate: '2026-03-19', paidDate: null },
  { id: 'INV-005', invoiceNumber: 'INV-2026-005', customer: 'FastShip Inc', amount: 950, tax: 71.25, total: 1021.25, status: 'PAID', issueDate: '2026-02-10', dueDate: '2026-03-10', paidDate: '2026-02-15' },
]

const statusColors = {
  PAID: 'bg-success/20 text-success',
  PENDING: 'bg-warning/20 text-warning',
  OVERDUE: 'bg-danger/20 text-danger',
}

export default function Invoices() {
  const [searchTerm, setSearchTerm] = useState('')
  
  // Fetch invoices from API
  const { data: apiInvoices = [], isLoading } = useQuery({
    queryKey: ['invoices'],
    queryFn: fetchInvoices,
  })
  
  // Use API data if available, otherwise mock data
  const invoices = apiInvoices.length > 0 ? apiInvoices : mockInvoices
  
  const filteredInvoices = invoices.filter(inv => 
    (inv.invoiceNumber || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (inv.customer || '').toLowerCase().includes(searchTerm.toLowerCase())
  )
  
  const stats = {
    total: invoices.reduce((acc, inv) => acc + (inv.total || inv.amount || 0), 0),
    paid: invoices.filter(i => i.status === 'PAID').reduce((acc, inv) => acc + (inv.total || inv.amount || 0), 0),
    pending: invoices.filter(i => i.status === 'PENDING').reduce((acc, inv) => acc + (inv.total || inv.amount || 0), 0),
    overdue: invoices.filter(i => i.status === 'OVERDUE').reduce((acc, inv) => acc + (inv.total || inv.amount || 0), 0),
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Invoices</h1>
          <p className="text-textSecondary mt-1">Manage billing and payments</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Create Invoice
        </button>
      </div>
      
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="glass-card p-4">
          <p className="text-textMuted text-sm">Total Invoiced</p>
          <p className="text-2xl font-bold text-textPrimary">${stats.total.toLocaleString()}</p>
        </div>
        <div className="glass-card p-4">
          <p className="text-textMuted text-sm">Paid</p>
          <p className="text-2xl font-bold text-success">${stats.paid.toLocaleString()}</p>
        </div>
        <div className="glass-card p-4">
          <p className="text-textMuted text-sm">Pending</p>
          <p className="text-2xl font-bold text-warning">${stats.pending.toLocaleString()}</p>
        </div>
        <div className="glass-card p-4">
          <p className="text-textMuted text-sm">Overdue</p>
          <p className="text-2xl font-bold text-danger">${stats.overdue.toLocaleString()}</p>
        </div>
      </div>
      
      {/* Search */}
      <div className="glass-card p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
          <input
            type="text"
            placeholder="Search invoices by number or customer..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field pl-10"
          />
        </div>
      </div>
      
      {/* Invoices Table */}
      <div className="glass-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-surfaceLighter/50">
                <th className="text-left p-4 text-textMuted font-medium">Invoice #</th>
                <th className="text-left p-4 text-textMuted font-medium">Customer</th>
                <th className="text-left p-4 text-textMuted font-medium">Amount</th>
                <th className="text-left p-4 text-textMuted font-medium">Tax</th>
                <th className="text-left p-4 text-textMuted font-medium">Total</th>
                <th className="text-left p-4 text-textMuted font-medium">Status</th>
                <th className="text-left p-4 text-textMuted font-medium">Issue Date</th>
                <th className="text-left p-4 text-textMuted font-medium">Due Date</th>
                <th className="text-left p-4 text-textMuted font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredInvoices.map((inv, index) => (
                <motion.tr
                  key={inv.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="border-b border-surfaceLighter/30 hover:bg-surfaceLight/30 transition-colors"
                >
                  <td className="p-4">
                    <span className="font-semibold text-primary">{inv.invoiceNumber}</span>
                  </td>
                  <td className="p-4 text-textSecondary">{inv.customer}</td>
                  <td className="p-4 text-textSecondary">${inv.amount.toLocaleString()}</td>
                  <td className="p-4 text-textSecondary">${inv.tax.toLocaleString()}</td>
                  <td className="p-4">
                    <span className="font-semibold text-textPrimary">${inv.total.toLocaleString()}</span>
                  </td>
                  <td className="p-4">
                    <span className={`status-badge ${statusColors[inv.status]}`}>
                      {inv.status}
                    </span>
                  </td>
                  <td className="p-4 text-textSecondary text-sm">{inv.issueDate}</td>
                  <td className="p-4 text-textSecondary text-sm">{inv.dueDate}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-2">
                      <button className="p-2 hover:bg-surfaceLight rounded-lg transition-colors" title="Download PDF">
                        <Download className="w-4 h-4 text-textMuted" />
                      </button>
                      <button className="p-2 hover:bg-surfaceLight rounded-lg transition-colors" title="Send Invoice">
                        <Send className="w-4 h-4 text-textMuted" />
                      </button>
                      <button className="p-2 hover:bg-surfaceLight rounded-lg transition-colors">
                        <MoreVertical className="w-4 h-4 text-textMuted" />
                      </button>
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
