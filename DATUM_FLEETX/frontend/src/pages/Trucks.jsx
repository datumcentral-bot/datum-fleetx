import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import { Plus, Search, Truck as TruckIcon, Fuel, Gauge, Calendar, MoreVertical, Edit, Trash2, Eye, X, Save } from 'lucide-react'
import api from '../services/api'
import { useAuthStore } from '../store/authStore'

// Fetch trucks from API
const fetchTrucks = async () => {
  try {
    const response = await api.get('/trucks')
    return response.data.data || []
  } catch (error) {
    console.error('Failed to fetch trucks:', error)
    return []
  }
}

// Create truck mutation
const createTruck = async (truckData) => {
  const response = await api.post('/trucks', truckData)
  return response.data.data
}

// Update truck mutation
const updateTruck = async ({ id, data }) => {
  const response = await api.put(`/trucks/${id}`, data)
  return response.data.data
}

// Delete truck mutation
const deleteTruck = async (id) => {
  const response = await api.delete(`/trucks/${id}`)
  return response.data
}

// Mock data for demo/offline
const mockTrucks = [
  { id: 'TRK-001', truckNumber: 'Truck-001', make: 'Freightliner', model: 'Cascadia', year: 2022, truckType: 'DRY_VAN', status: 'AVAILABLE', mileage: 125000, fuelEfficiency: 7.2, currentLocation: 'Dallas, TX' },
  { id: 'TRK-002', truckNumber: 'Truck-002', make: 'Kenworth', model: 'T680', year: 2021, truckType: 'REEFER', status: 'IN_TRANSIT', mileage: 180000, fuelEfficiency: 6.8, currentLocation: 'Chicago, IL' },
  { id: 'TRK-003', truckNumber: 'Truck-003', make: 'Peterbilt', model: '579', year: 2023, truckType: 'FLATBED', status: 'MAINTENANCE', mileage: 45000, fuelEfficiency: 7.5, currentLocation: 'Los Angeles, CA' },
  { id: 'TRK-004', truckNumber: 'Truck-004', make: 'Volvo', model: 'VNL', year: 2022, truckType: 'DRY_VAN', status: 'AVAILABLE', mileage: 98000, fuelEfficiency: 7.8, currentLocation: 'Miami, FL' },
  { id: 'TRK-005', truckNumber: 'Truck-005', make: 'Mack', model: 'Anthem', year: 2021, truckType: 'REEFER', status: 'IN_TRANSIT', mileage: 145000, fuelEfficiency: 6.5, currentLocation: 'Atlanta, GA' },
  { id: 'TRK-006', truckNumber: 'Truck-006', make: 'International', model: 'LT', year: 2020, truckType: 'FLATBED', status: 'AVAILABLE', mileage: 210000, fuelEfficiency: 6.9, currentLocation: 'Phoenix, AZ' },
]

const statusColors = {
  AVAILABLE: 'bg-success/20 text-success',
  ASSIGNED: 'bg-primary/20 text-primary',
  IN_TRANSIT: 'bg-accent/20 text-accent',
  MAINTENANCE: 'bg-warning/20 text-warning',
  OUT_OF_SERVICE: 'bg-danger/20 text-danger',
}

const truckTypes = {
  DRY_VAN: 'Dry Van',
  REEFER: 'Reefer',
  FLATBED: 'Flatbed',
  TANKER: 'Tanker',
  BOX_TRUCK: 'Box Truck',
}

export default function Trucks() {
  const [searchTerm, setSearchTerm] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [editingTruck, setEditingTruck] = useState(null)
  const [formData, setFormData] = useState({
    truckNumber: '',
    make: '',
    model: '',
    year: new Date().getFullYear(),
    plateNumber: '',
    truckType: 'DRY_VAN',
    status: 'AVAILABLE',
    capacityWeight: null,
    capacityVolume: null,
    fuelType: 'Diesel',
    fuelEfficiency: null,
    currentMileage: null,
    vin: ''
  })
  const [errors, setErrors] = useState({})
  const queryClient = useQueryClient()
  
  // Create mutation
  const createMutation = useMutation({
    mutationFn: createTruck,
    onSuccess: () => {
      queryClient.invalidateQueries(['trucks'])
      setShowModal(false)
      resetForm()
    },
    onError: (error) => {
      const message = error.response?.data?.message || 'Failed to create truck'
      setErrors({ submit: message })
    }
  })
  
  // Update mutation
  const updateMutation = useMutation({
    mutationFn: updateTruck,
    onSuccess: () => {
      queryClient.invalidateQueries(['trucks'])
      setShowModal(false)
      setEditingTruck(null)
      resetForm()
    },
    onError: (error) => {
      const message = error.response?.data?.message || 'Failed to update truck'
      setErrors({ submit: message })
    }
  })
  
  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: deleteTruck,
    onSuccess: () => {
      queryClient.invalidateQueries(['trucks'])
    },
    onError: (error) => {
      const message = error.response?.data?.message || 'Failed to delete truck'
      alert(message)
    }
  })
  
  // Reset form
  const resetForm = () => {
    setFormData({
      truckNumber: '',
      make: '',
      model: '',
      year: new Date().getFullYear(),
      plateNumber: '',
      truckType: 'DRY_VAN',
      status: 'AVAILABLE',
      capacityWeight: null,
      capacityVolume: null,
      fuelType: 'Diesel',
      fuelEfficiency: null,
      currentMileage: null,
      vin: ''
    })
    setErrors({})
  }
  
  // Handle form submit
  const handleSubmit = (e) => {
    e.preventDefault()
    setErrors({})
    
    if (editingTruck) {
      updateMutation.mutate({ id: editingTruck.id, data: formData })
    } else {
      createMutation.mutate(formData)
    }
  }
  
  // Open edit modal
  const handleEdit = (truck) => {
    setEditingTruck(truck)
    setFormData({
      truckNumber: truck.truckNumber || '',
      make: truck.make || '',
      model: truck.model || '',
      year: truck.year || new Date().getFullYear(),
      plateNumber: truck.plateNumber || '',
      truckType: truck.truckType || 'DRY_VAN',
      status: truck.status || 'AVAILABLE',
      capacityWeight: truck.capacityWeight || null,
      capacityVolume: truck.capacityVolume || null,
      fuelType: truck.fuelType || 'Diesel',
      fuelEfficiency: truck.fuelEfficiency || null,
      currentMileage: truck.currentMileage || null,
      vin: truck.vin || ''
    })
    setShowModal(true)
  }
  
  // Handle delete
  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this truck?')) {
      deleteMutation.mutate(id)
    }
  }
  
  // Fetch trucks from API
  const { data: apiTrucks = [], isLoading } = useQuery({
    queryKey: ['trucks'],
    queryFn: fetchTrucks,
  })
  
  // Use API data if available, otherwise mock data
  const trucks = apiTrucks.length > 0 ? apiTrucks : mockTrucks
  
  const filteredTrucks = trucks.filter(truck => 
    (truck.truckNumber || truck.number || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (truck.make || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (truck.model || '').toLowerCase().includes(searchTerm.toLowerCase())
  )
  
  const stats = {
    total: trucks.length,
    available: trucks.filter(t => t.status === 'AVAILABLE').length,
    inTransit: trucks.filter(t => t.status === 'IN_TRANSIT').length,
    maintenance: trucks.filter(t => t.status === 'MAINTENANCE').length,
  }
  
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Trucks</h1>
          <p className="text-textSecondary mt-1">Manage your fleet vehicles</p>
        </div>
        <button 
          className="btn-primary flex items-center gap-2"
          onClick={() => { resetForm(); setEditingTruck(null); setShowModal(true); }}
        >
          <Plus className="w-5 h-5" />
          Add Truck
        </button>
      </div>
      
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Total Trucks', value: stats.total },
          { label: 'Available', value: stats.available, color: 'text-success' },
          { label: 'In Transit', value: stats.inTransit, color: 'text-accent' },
          { label: 'Maintenance', value: stats.maintenance, color: 'text-warning' },
        ].map((stat) => (
          <div key={stat.label} className="glass-card p-4">
            <p className="text-textMuted text-sm">{stat.label}</p>
            <p className={`text-2xl font-bold ${stat.color || 'text-textPrimary'}`}>{stat.value}</p>
          </div>
        ))}
      </div>
      
      {/* Search and Filter */}
      <div className="glass-card p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
          <input
            type="text"
            placeholder="Search trucks by number, make, or model..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field pl-10"
          />
        </div>
      </div>
      
      {/* Trucks Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredTrucks.map((truck, index) => (
          <motion.div
            key={truck.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="glass-card p-6 hover:border-primary/30 transition-colors"
          >
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="p-3 rounded-xl bg-primary/10">
                  <TruckIcon className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-textPrimary">{truck.number}</h3>
                  <p className="text-textMuted text-sm">{truck.make} {truck.model}</p>
                </div>
              </div>
              <button className="p-2 hover:bg-surfaceLight rounded-lg">
                <MoreVertical className="w-4 h-4 text-textMuted" />
              </button>
            </div>
            
            <div className="flex items-center gap-2 mb-4">
              <span className={`status-badge ${statusColors[truck.status]}`}>
                {truck.status.replace('_', ' ')}
              </span>
              <span className="text-textMuted text-sm">{truckTypes[truck.type]}</span>
            </div>
            
            <div className="grid grid-cols-2 gap-4 pt-4 border-t border-surfaceLighter/50">
              <div className="flex items-center gap-2">
                <Gauge className="w-4 h-4 text-textMuted" />
                <span className="text-textSecondary text-sm">{truck.mileage.toLocaleString()} mi</span>
              </div>
              <div className="flex items-center gap-2">
                <Fuel className="w-4 h-4 text-textMuted" />
                <span className="text-textSecondary text-sm">{truck.fuelEfficiency} mpg</span>
              </div>
              <div className="flex items-center gap-2 col-span-2">
                <Calendar className="w-4 h-4 text-textMuted" />
                <span className="text-textSecondary text-sm">{truck.location}</span>
              </div>
            </div>
            
            {/* Quick Actions */}
            <div className="flex items-center gap-2 mt-4 pt-4 border-t border-surfaceLighter/50">
              <button className="flex-1 flex items-center justify-center gap-1 py-2 text-sm text-primary hover:bg-primary/10 rounded-lg transition-colors">
                <Eye className="w-4 h-4" />
                View
              </button>
              <button className="flex-1 flex items-center justify-center gap-1 py-2 text-sm text-textSecondary hover:bg-surfaceLight rounded-lg transition-colors">
                <Edit className="w-4 h-4" />
                Edit
              </button>
            </div>
          </motion.div>
        ))}
      </div>
      
      {filteredTrucks.length === 0 && (
        <div className="glass-card p-12 text-center">
          <TruckIcon className="w-12 h-12 text-textMuted mx-auto mb-4" />
          <p className="text-textSecondary">No trucks found matching your search</p>
        </div>
      )}
    </div>
  )
}
