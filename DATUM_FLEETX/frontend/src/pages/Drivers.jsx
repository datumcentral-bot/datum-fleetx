import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Plus, Search, Users, Phone, Mail, Shield, MoreVertical, MapPin, Clock, Star } from 'lucide-react'
import api from '../services/api'

// Fetch drivers from API
const fetchDrivers = async () => {
  try {
    const response = await api.get('/drivers')
    return response.data.data || []
  } catch (error) {
    console.error('Failed to fetch drivers:', error)
    return []
  }
}

// Mock data for demo/offline mode
const mockDrivers = [
  { id: 1, firstName: 'John', lastName: 'Smith', phone: '+1 555-0101', email: 'john@demo.com', status: 'AVAILABLE', safetyScore: 98, totalMiles: 125000, currentLocation: 'Dallas, TX', licenseExpiry: '2026-08-15', rating: 4.9 },
  { id: 2, firstName: 'Sarah', lastName: 'Johnson', phone: '+1 555-0102', email: 'sarah@demo.com', status: 'ON_DUTY', safetyScore: 100, totalMiles: 98000, currentLocation: 'Chicago, IL', licenseExpiry: '2026-11-20', rating: 5.0 },
  { id: 3, firstName: 'Mike', lastName: 'Davis', phone: '+1 555-0103', email: 'mike@demo.com', status: 'AVAILABLE', safetyScore: 95, totalMiles: 150000, currentLocation: 'Los Angeles, CA', licenseExpiry: '2025-12-01', rating: 4.7 },
  { id: 4, firstName: 'Emily', lastName: 'Wilson', phone: '+1 555-0104', email: 'emily@demo.com', status: 'OFF_DUTY', safetyScore: 99, totalMiles: 87000, currentLocation: 'Miami, FL', licenseExpiry: '2026-03-10', rating: 4.8 },
  { id: 5, firstName: 'Robert', lastName: 'Brown', phone: '+1 555-0105', email: 'robert@demo.com', status: 'ON_DUTY', safetyScore: 97, totalMiles: 112000, currentLocation: 'Atlanta, GA', licenseExpiry: '2026-06-22', rating: 4.6 },
  { id: 6, firstName: 'Lisa', lastName: 'Martinez', phone: '+1 555-0106', email: 'lisa@demo.com', status: 'AVAILABLE', safetyScore: 100, totalMiles: 76000, currentLocation: 'Phoenix, AZ', licenseExpiry: '2027-01-15', rating: 4.9 },
]

const statusColors = {
  AVAILABLE: 'bg-success/20 text-success',
  ON_DUTY: 'bg-primary/20 text-primary',
  OFF_DUTY: 'bg-textMuted/20 text-textMuted',
  ON_LEAVE: 'bg-warning/20 text-warning',
}

export default function Drivers() {
  const [searchTerm, setSearchTerm] = useState('')
  
  // Fetch drivers from API
  const { data: apiDrivers = [], isLoading } = useQuery({
    queryKey: ['drivers'],
    queryFn: fetchDrivers,
  })
  
  // Use API data if available, otherwise mock data
  const drivers = apiDrivers.length > 0 ? apiDrivers : mockDrivers
  
  // Helper function to get full name
  const getDriverName = (driver) => {
    if (driver.firstName && driver.lastName) {
      return `${driver.firstName} ${driver.lastName}`
    }
    return driver.name || driver.firstName || 'Unknown Driver'
  }
  
  const filteredDrivers = drivers.filter(driver => 
    getDriverName(driver).toLowerCase().includes(searchTerm.toLowerCase()) ||
    (driver.email || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (driver.phone || '').includes(searchTerm)
  )
  
  const stats = {
    total: drivers.length,
    available: drivers.filter(d => d.status === 'AVAILABLE').length,
    onDuty: drivers.filter(d => d.status === 'ON_DUTY').length,
    avgSafety: Math.round(drivers.reduce((acc, d) => acc + (d.safetyScore || 95), 0) / drivers.length),
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Drivers</h1>
          <p className="text-textSecondary mt-1">Manage your driver roster</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Add Driver
        </button>
      </div>
      
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Total Drivers', value: stats.total },
          { label: 'Available', value: stats.available, color: 'text-success' },
          { label: 'On Duty', value: stats.onDuty, color: 'text-primary' },
          { label: 'Avg Safety', value: `${stats.avgSafety}%`, color: 'text-accent' },
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
            placeholder="Search drivers by name, email, or phone..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field pl-10"
          />
        </div>
      </div>
      
      {/* Drivers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredDrivers.map((driver, index) => (
          <motion.div
            key={driver.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="glass-card p-6 hover:border-primary/30 transition-colors"
          >
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold">
                  {driver.name.split(' ').map(n => n[0]).join('')}
                </div>
                <div>
                  <h3 className="font-semibold text-textPrimary">{driver.name}</h3>
                  <div className="flex items-center gap-1 text-warning">
                    <Star className="w-3 h-3 fill-current" />
                    <span className="text-sm">{driver.rating}</span>
                  </div>
                </div>
              </div>
              <button className="p-2 hover:bg-surfaceLight rounded-lg">
                <MoreVertical className="w-4 h-4 text-textMuted" />
              </button>
            </div>
            
            <span className={`status-badge ${statusColors[driver.status]}`}>
              {driver.status.replace('_', ' ')}
            </span>
            
            <div className="mt-4 space-y-2 pt-4 border-t border-surfaceLighter/50">
              <div className="flex items-center gap-2 text-textSecondary text-sm">
                <Phone className="w-4 h-4 text-textMuted" />
                {driver.phone}
              </div>
              <div className="flex items-center gap-2 text-textSecondary text-sm">
                <Mail className="w-4 h-4 text-textMuted" />
                {driver.email}
              </div>
              <div className="flex items-center gap-2 text-textSecondary text-sm">
                <MapPin className="w-4 h-4 text-textMuted" />
                {driver.currentLocation}
              </div>
            </div>
            
            <div className="flex items-center justify-between mt-4 pt-4 border-t border-surfaceLighter/50">
              <div className="flex items-center gap-2">
                <Shield className="w-4 h-4 text-success" />
                <span className="text-sm text-textSecondary">Safety: {driver.safetyScore}%</span>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-textMuted" />
                <span className="text-sm text-textSecondary">{driver.totalMiles.toLocaleString()} mi</span>
              </div>
            </div>
            
            {/* Quick Actions */}
            <div className="flex items-center gap-2 mt-4 pt-4 border-t border-surfaceLighter/50">
              <button className="flex-1 py-2 text-sm text-primary hover:bg-primary/10 rounded-lg transition-colors">
                View Profile
              </button>
              <button className="flex-1 py-2 text-sm text-textSecondary hover:bg-surfaceLight rounded-lg transition-colors">
                Assign Load
              </button>
            </div>
          </motion.div>
        ))}
      </div>
      
      {filteredDrivers.length === 0 && (
        <div className="glass-card p-12 text-center">
          <Users className="w-12 h-12 text-textMuted mx-auto mb-4" />
          <p className="text-textSecondary">No drivers found matching your search</p>
        </div>
      )}
    </div>
  )
}
