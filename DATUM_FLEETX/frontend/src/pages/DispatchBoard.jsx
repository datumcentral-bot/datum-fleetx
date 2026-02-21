import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Calendar, List, Map, Plus, Filter, Clock, MapPin, Truck, User } from 'lucide-react'
import api from '../services/api'

// Fetch loads for dispatch board from API
const fetchDispatchLoads = async () => {
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
  { id: 'LD-001', customer: 'ABC Logistics', pickup: 'Los Angeles, CA', delivery: 'Phoenix, AZ', date: '2026-02-20', status: 'created', driver: null, truck: null },
  { id: 'LD-002', customer: 'XYZ Freight', pickup: 'Dallas, TX', delivery: 'Houston, TX', date: '2026-02-20', status: 'dispatched', driver: 'John Smith', truck: 'Truck-001' },
  { id: 'LD-003', customer: 'Global Cargo', pickup: 'Miami, FL', delivery: 'Atlanta, GA', date: '2026-02-21', status: 'in_transit', driver: 'Sarah Johnson', truck: 'Truck-002' },
  { id: 'LD-004', customer: 'Metro Shipping', pickup: 'Chicago, IL', delivery: 'Detroit, MI', date: '2026-02-21', status: 'assigned', driver: 'Mike Davis', truck: null },
]

const columns = [
  { id: 'created', title: 'Created', color: 'border-textMuted' },
  { id: 'dispatched', title: 'Dispatched', color: 'border-warning' },
  { id: 'in_transit', title: 'In Transit', color: 'border-primary' },
  { id: 'delivered', title: 'Delivered', color: 'border-success' },
]

const statusColors = {
  created: 'bg-textMuted/20 text-textMuted',
  dispatched: 'bg-warning/20 text-warning',
  in_transit: 'bg-primary/20 text-primary',
  assigned: 'bg-accent/20 text-accent',
  delivered: 'bg-success/20 text-success',
}

export default function DispatchBoard() {
  const [view, setView] = useState('board')
  
  // Fetch loads from API
  const { data: apiLoads = [], isLoading } = useQuery({
    queryKey: ['dispatchLoads'],
    queryFn: fetchDispatchLoads,
  })
  
  // Use API data if available, otherwise mock data
  const loads = apiLoads.length > 0 ? apiLoads : mockLoads
  
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Dispatch Board</h1>
          <p className="text-textSecondary mt-1">Drag and drop loads to manage assignments</p>
        </div>
        <div className="flex items-center gap-2">
          <div className="glass-button flex items-center gap-2">
            <button onClick={() => setView('board')} className={`p-2 rounded-lg ${view === 'board' ? 'bg-primary/20 text-primary' : ''}`}>
              <List className="w-4 h-4" />
            </button>
            <button onClick={() => setView('calendar')} className={`p-2 rounded-lg ${view === 'calendar' ? 'bg-primary/20 text-primary' : ''}`}>
              <Calendar className="w-4 h-4" />
            </button>
            <button onClick={() => setView('map')} className={`p-2 rounded-lg ${view === 'map' ? 'bg-primary/20 text-primary' : ''}`}>
              <Map className="w-4 h-4" />
            </button>
          </div>
          <button className="btn-primary flex items-center gap-2">
            <Plus className="w-5 h-5" />
            New Load
          </button>
        </div>
      </div>
      
      {/* Kanban Board */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {columns.map((column) => (
          <div key={column.id} className="glass-card p-4">
            <div className={`flex items-center gap-2 pb-3 border-b-2 ${column.color} mb-4`}>
              <h3 className="font-semibold text-textPrimary">{column.title}</h3>
              <span className="text-textMuted text-sm">
                {mockLoads.filter(l => l.status === column.id).length}
              </span>
            </div>
            
            <div className="space-y-3">
              {mockLoads
                .filter((load) => load.status === column.id)
                .map((load, index) => (
                  <motion.div
                    key={load.id}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: index * 0.05 }}
                    className="bg-surfaceLight/50 p-4 rounded-xl border border-surfaceLighter/30 hover:border-primary/30 cursor-grab active:cursor-grabbing transition-colors"
                  >
                    <div className="flex items-start justify-between mb-2">
                      <span className="font-semibold text-primary">{load.id}</span>
                      <span className={`status-badge ${statusColors[load.status]}`}>
                        {load.status.replace('_', ' ')}
                      </span>
                    </div>
                    
                    <p className="text-textPrimary font-medium">{load.customer}</p>
                    
                    <div className="mt-3 space-y-2 text-sm">
                      <div className="flex items-center gap-2 text-textSecondary">
                        <MapPin className="w-3 h-3 text-textMuted" />
                        {load.pickup}
                      </div>
                      <div className="flex items-center gap-2 text-textSecondary">
                        <MapPin className="w-3 h-3 text-textMuted" />
                        {load.delivery}
                      </div>
                    </div>
                    
                    <div className="mt-3 pt-3 border-t border-surfaceLighter/30 flex items-center justify-between">
                      <div className="flex items-center gap-2 text-textMuted text-xs">
                        <Clock className="w-3 h-3" />
                        {load.date}
                      </div>
                      {load.driver && (
                        <div className="flex items-center gap-1 text-textSecondary text-xs">
                          <User className="w-3 h-3" />
                          {load.driver}
                        </div>
                      )}
                      {load.truck && (
                        <div className="flex items-center gap-1 text-textSecondary text-xs">
                          <Truck className="w-3 h-3" />
                          {load.truck}
                        </div>
                      )}
                    </div>
                  </motion.div>
                ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
