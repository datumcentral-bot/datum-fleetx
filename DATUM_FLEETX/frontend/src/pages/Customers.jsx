import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Plus, Search, Building, MapPin, Phone, Mail, DollarSign, TrendingUp, Clock } from 'lucide-react'
import api from '../services/api'

// Fetch customers from API
const fetchCustomers = async () => {
  try {
    const response = await api.get('/customers')
    return response.data.data || []
  } catch (error) {
    console.error('Failed to fetch customers:', error)
    return []
  }
}

// Mock data for demo/offline mode
const mockCustomers = [
  { id: 1, companyName: 'ABC Logistics', contactPerson: 'Robert Brown', email: 'robert@abclogistics.com', phone: '+1 555-1001', city: 'Los Angeles', country: 'USA', creditLimit: 50000, balance: 12500, totalLoads: 45, revenue: 125000 },
  { id: 2, companyName: 'XYZ Freight', contactPerson: 'Lisa Chen', email: 'lisa@xyzfreight.com', phone: '+1 555-1002', city: 'Dallas', country: 'USA', creditLimit: 75000, balance: 8200, totalLoads: 32, revenue: 98000 },
  { id: 3, companyName: 'Global Cargo', contactPerson: 'James Wilson', email: 'james@globalcargo.com', phone: '+1 555-1003', city: 'Miami', country: 'USA', creditLimit: 100000, balance: 35000, totalLoads: 78, revenue: 245000 },
  { id: 4, companyName: 'FastShip Inc', contactPerson: 'Maria Garcia', email: 'maria@fastship.com', phone: '+1 555-1004', city: 'Chicago', country: 'USA', creditLimit: 60000, balance: 15800, totalLoads: 56, revenue: 156000 },
  { id: 5, companyName: 'Premier Logistics', contactPerson: 'David Lee', email: 'david@premierlogistics.com', phone: '+1 555-1005', city: 'Atlanta', country: 'USA', creditLimit: 80000, balance: 22000, totalLoads: 63, revenue: 189000 },
  { id: 6, companyName: 'TransGlobal', contactPerson: 'Sarah Taylor', email: 'sarah@transglobal.com', phone: '+1 555-1006', city: 'Phoenix', country: 'USA', creditLimit: 90000, balance: 4500, totalLoads: 41, revenue: 134000 },
]

export default function Customers() {
  const [searchTerm, setSearchTerm] = useState('')
  
  // Fetch customers from API
  const { data: apiCustomers = [], isLoading } = useQuery({
    queryKey: ['customers'],
    queryFn: fetchCustomers,
  })
  
  // Use API data if available, otherwise mock data
  const customers = apiCustomers.length > 0 ? apiCustomers : mockCustomers
  
  // Helper function to get customer name
  const getCustomerName = (customer) => customer.companyName || customer.name || 'Unknown'
  const getContactName = (customer) => customer.contactPerson || customer.contact || 'N/A'
  
  const filteredCustomers = customers.filter(customer => 
    getCustomerName(customer).toLowerCase().includes(searchTerm.toLowerCase()) ||
    getContactName(customer).toLowerCase().includes(searchTerm.toLowerCase()) ||
    (customer.email || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (customer.city || '').toLowerCase().includes(searchTerm.toLowerCase())
  )
  
  const stats = {
    total: customers.length,
    totalRevenue: customers.reduce((acc, c) => acc + (c.revenue || 0), 0),
    totalBalance: customers.reduce((acc, c) => acc + (c.balance || 0), 0),
    avgCreditLimit: Math.round(customers.reduce((acc, c) => acc + (c.creditLimit || 0), 0) / customers.length),
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Customers</h1>
          <p className="text-textSecondary mt-1">Manage your shippers and clients</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Add Customer
        </button>
      </div>
      
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Total Customers', value: stats.total },
          { label: 'Total Revenue', value: `$${(stats.totalRevenue / 1000).toFixed(0)}K`, color: 'text-success' },
          { label: 'Outstanding', value: `$${stats.totalBalance.toLocaleString()}`, color: 'text-warning' },
          { label: 'Avg Credit Limit', value: `$${stats.avgCreditLimit.toLocaleString()}`, color: 'text-primary' },
        ].map((stat) => (
          <div key={stat.label} className="glass-card p-4">
            <p className="text-textMuted text-sm">{stat.label}</p>
            <p className={`text-2xl font-bold ${stat.color || 'text-textPrimary'}`}>{stat.value}</p>
          </div>
        ))}
      </div>
      
      {/* Search */}
      <div className="glass-card p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
          <input
            type="text"
            placeholder="Search customers by name, contact, email, or city..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field pl-10"
          />
        </div>
      </div>
      
      {/* Customers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredCustomers.map((customer, index) => (
          <motion.div
            key={customer.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="glass-card p-6 hover:border-primary/30 transition-colors"
          >
            <div className="flex items-start gap-4 mb-4">
              <div className="p-3 rounded-xl bg-accent/10">
                <Building className="w-6 h-6 text-accent" />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-textPrimary">{customer.name}</h3>
                <p className="text-textMuted text-sm">{customer.contact}</p>
              </div>
              <div className="text-right">
                <p className="text-xs text-textMuted">Loads</p>
                <p className="font-semibold text-primary">{customer.totalLoads}</p>
              </div>
            </div>
            
            <div className="space-y-2 text-sm">
              <div className="flex items-center gap-2 text-textSecondary">
                <Mail className="w-4 h-4 text-textMuted" />
                {customer.email}
              </div>
              <div className="flex items-center gap-2 text-textSecondary">
                <Phone className="w-4 h-4 text-textMuted" />
                {customer.phone}
              </div>
              <div className="flex items-center gap-2 text-textSecondary">
                <MapPin className="w-4 h-4 text-textMuted" />
                {customer.city}, {customer.country}
              </div>
            </div>
            
            <div className="mt-4 pt-4 border-t border-surfaceLighter/50">
              <div className="flex justify-between mb-2">
                <div>
                  <p className="text-textMuted text-xs">Credit Limit</p>
                  <p className="font-semibold text-textPrimary">${customer.creditLimit.toLocaleString()}</p>
                </div>
                <div className="text-right">
                  <p className="text-textMuted text-xs">Balance</p>
                  <p className={`font-semibold ${customer.balance > customer.creditLimit * 0.5 ? 'text-danger' : 'text-warning'}`}>
                    ${customer.balance.toLocaleString()}
                  </p>
                </div>
              </div>
              
              <div className="flex items-center gap-2 text-success pt-2 border-t border-surfaceLighter/30">
                <TrendingUp className="w-4 h-4" />
                <span className="text-sm">${customer.revenue.toLocaleString()} revenue</span>
              </div>
            </div>
            
            {/* Quick Actions */}
            <div className="flex items-center gap-2 mt-4 pt-4 border-t border-surfaceLighter/50">
              <button className="flex-1 py-2 text-sm text-primary hover:bg-primary/10 rounded-lg transition-colors">
                View Details
              </button>
              <button className="flex-1 py-2 text-sm text-textSecondary hover:bg-surfaceLight rounded-lg transition-colors">
                Create Load
              </button>
            </div>
          </motion.div>
        ))}
      </div>
      
      {filteredCustomers.length === 0 && (
        <div className="glass-card p-12 text-center">
          <Building className="w-12 h-12 text-textMuted mx-auto mb-4" />
          <p className="text-textSecondary">No customers found matching your search</p>
        </div>
      )}
    </div>
  )
}
