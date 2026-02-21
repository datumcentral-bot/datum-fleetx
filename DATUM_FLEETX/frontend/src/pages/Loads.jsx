import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Plus, Search, Filter, MoreVertical, Route, MapPin, Clock, DollarSign, Truck, Package } from 'lucide-react'
import api from '../services/api'

// Fetch loads from API
const fetchLoads = async () => {
  try {
    const response = await api.get('/loads')
    return response.data.data || []
  } catch (error) {
    console.error('Failed to fetch loads:', error)
    return []
  }
}

// Mock data for demo/offline mode
const mockLoads = [
  { id: 'LD-001', customer: 'ABC Logistics', origin: 'Los Angeles, CA', destination: 'Phoenix, AZ', rate: 2500, status: 'IN_TRANSIT', pickupDate: '2026-02-20', deliveryDate: '2026-02-21', distance: 370, weight: 42000, commodity: 'Electronics' },
  { id: 'LD-002', customer: 'XYZ Freight', origin: 'Dallas, TX', destination: 'Houston, TX', rate: 1200, status: 'DELIVERED', pickupDate: '2026-02-18', deliveryDate: '2026-02-18', distance: 240, weight: 18000, commodity: 'Furniture' },
  { id: 'LD-003', customer: 'Global Cargo', origin: 'Miami, FL', destination: 'Atlanta, GA', rate: 3100, status: 'DISPATCHED', pickupDate: '2026-02-21', deliveryDate: '2026-02-22', distance: 660, weight: 45000, commodity: 'Automotive Parts' },
  { id: 'LD-004', customer: 'Metro Shipping', origin: 'Chicago, IL', destination: 'Detroit, MI', rate: 1800, status: 'CREATED', pickupDate: '2026-02-22', deliveryDate: '2026-02-23', distance: 280, weight: 22000, commodity: 'Consumer Goods' },
  { id: 'LD-005', customer: 'Coastal Transport', origin: 'Seattle, WA', destination: 'Portland, OR', rate: 950, status: 'PICKED_UP', pickupDate: '2026-02-19', deliveryDate: '2026-02-20', distance: 174, weight: 15000, commodity: 'Food Products' },
  { id: 'LD-006', customer: 'FastShip Inc', origin: 'Denver, CO', destination: 'Salt Lake City, UT', rate: 2200, status: 'IN_TRANSIT', pickupDate: '2026-02-19', deliveryDate: '2026-02-20', distance: 520, weight: 38000, commodity: 'Medical Supplies' },
  { id: 'LD-007', customer: 'Premier Logistics', origin: 'Boston, MA', destination: 'New York, NY', rate: 1650, status: 'DELIVERED', pickupDate: '2026-02-17', deliveryDate: '2026-02-17', distance: 215, weight: 12000, commodity: 'Clothing' },
  { id: 'LD-008', customer: 'TransGlobal', origin: 'San Francisco, CA', destination: 'Las Vegas, NV', rate: 2800, status: 'CREATED', pickupDate: '2026-02-23', deliveryDate: '2026-02-24', distance: 570, weight: 40000, commodity: 'Machinery' },
]

const statusColors = {
  CREATED: 'bg-textMuted/20 text-textMuted',
  DISPATCHED: 'bg-warning/20 text-warning',
  PICKED_UP: 'bg-primary/20 text-primary',
  IN_TRANSIT: 'bg-accent/20 text-accent',
  DELIVERED: 'bg-success/20 text-success',
}

export default function Loads() {
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState('all')
  
  // Fetch loads from API
  const { data: apiLoads = [], isLoading } = useQuery({
    queryKey: ['loads'],
    queryFn: fetchLoads,
  })
  
  // Use API data if available, otherwise mock data
  const loads = apiLoads.length > 0 ? apiLoads : mockLoads
  
  const filteredLoads = loads.filter(load => {
    const matchesSearch = 
      (load.loadNumber || load.id || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (load.customer || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (load.origin || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (load.destination || '').toLowerCase().includes(searchTerm.toLowerCase())
    const matchesStatus = statusFilter === 'all' || load.status === statusFilter
    return matchesSearch && matchesStatus
  })
  
  const stats = {
    total: loads.length,
    active: loads.filter(l => l.status === 'IN_TRANSIT' || l.status === 'PICKED_UP').length,
    delivered: loads.filter(l => l.status === 'DELIVERED').length,
    totalRevenue: loads.reduce((acc, l) => acc + (l.rate || l.rateAmount || 0), 0),
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Loads</h1>
          <p className="text-textSecondary mt-1">Manage your shipments and deliveries</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Create Load
        </button>
      </div>
      
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Total Loads', value: stats.total },
          { label: 'Active', value: stats.active, color: 'text-accent' },
          { label: 'Delivered', value: stats.delivered, color: 'text-success' },
          { label: 'Revenue', value: `$${stats.totalRevenue.toLocaleString()}`, color: 'text-primary' },
        ].map((stat) => (
          <div key={stat.label} className="glass-card p-4">
            <p className="text-textMuted text-sm">{stat.label}</p>
            <p className={`text-2xl font-bold ${stat.color || 'text-textPrimary'}`}>{stat.value}</p>
          </div>
        ))}
      </div>
      
      {/* Filters */}
      <div className="glass-card p-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
            <input
              type="text"
              placeholder="Search loads by ID, customer, origin, or destination..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input-field pl-10"
            />
          </div>
          <select 
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="input-field"
          >
            <option value="all">All Status</option>
            <option value="CREATED">Created</option>
            <option value="DISPATCHED">Dispatched</option>
            <option value="PICKED_UP">Picked Up</option>
            <option value="IN_TRANSIT">In Transit</option>
            <option value="DELIVERED">Delivered</option>
          </select>
        </div>
      </div>
      
      {/* loads Table */}
      <div className="glass-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-surfaceLighter/50">
                <th className="text-left p-4 text-textMuted font-medium">Load #</th>
                <th className="text-left p-4 text-textMuted font-medium">Customer</th>
                <th className="text-left p-4 text-textMuted font-medium">Origin → Destination</th>
                <th className="text-left p-4 text-textMuted font-medium">Commodity</th>
                <th className="text-left p-4 text-textMuted font-medium">Rate</th>
                <th className="text-left p-4 text-textMuted font-medium">Status</th>
                <th className="text-left p-4 text-textMuted font-medium">Pickup</th>
                <th className="text-left p-4 text-textMuted font-medium">Delivery</th>
                <th className="text-left p-4 text-textMuted font-medium"></th>
              </tr>
            </thead>
            <tbody>
              {filteredLoads.map((load, index) => (
                <motion.tr
                  key={load.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="border-b border-surfaceLighter/30 hover:bg-surfaceLight/30 transition-colors"
                >
                  <td className="p-4">
                    <span className="font-semibold text-primary">{load.id}</span>
                  </td>
                  <td className="p-4 text-textSecondary">{load.customer}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-2 text-textSecondary">
                      <MapPin className="w-4 h-4 text-success" />
                      {load.origin}
                      <span className="text-textMuted mx-1">→</span>
                      <MapPin className="w-4 h-4 text-danger" />
                      {load.destination}
                    </div>
                    <div className="flex items-center gap-1 text-textMuted text-xs mt-1">
                      <Route className="w-3 h-3" />
                      {load.distance} miles
                    </div>
                  </td>
                  <td className="p-4">
                    <div className="flex items-center gap-2 text-textSecondary">
                      <Package className="w-4 h-4 text-textMuted" />
                      {load.commodity}
                    </div>
                    <div className="text-textMuted text-xs">{(load.weight / 1000).toFixed(1)}K lbs</div>
                  </td>
                  <td className="p-4">
                    <span className="font-semibold text-success">${load.rate.toLocaleString()}</span>
                  </td>
                  <td className="p-4">
                    <span className={`status-badge ${statusColors[load.status]}`}>
                      {load.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="p-4 text-textSecondary text-sm">{load.pickupDate}</td>
                  <td className="p-4 text-textSecondary text-sm">{load.deliveryDate}</td>
                  <td className="p-4">
                    <button className="p-2 hover:bg-surfaceLight rounded-lg transition-colors">
                      <MoreVertical className="w-4 h-4 text-textMuted" />
                    </button>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {filteredLoads.length === 0 && (
          <div className="p-12 text-center">
            <Route className="w-12 h-12 text-textMuted mx-auto mb-4" />
            <p className="text-textSecondary">No loads found matching your criteria</p>
          </div>
        )}
      </div>
    </div>
  )
}
