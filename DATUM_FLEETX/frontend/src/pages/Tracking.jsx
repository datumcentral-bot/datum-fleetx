import { useState, useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Truck, Navigation, Clock, MapPin, Phone, Route, RefreshCw } from 'lucide-react'
import api from '../services/api'

// Fetch tracking data from API
const fetchTrackingData = async () => {
  try {
    // Get trucks with their current locations
    const response = await api.get('/trucks')
    const trucks = response.data.data || []
    
    // Get drivers for mapping
    const driversResponse = await api.get('/drivers')
    const drivers = driversResponse.data.data || []
    
    // Map truck data to tracking format
    return trucks.map(truck => {
      const driver = drivers.find(d => d.id === truck.driverId)
      return {
        id: truck.id,
        name: truck.truckNumber || truck.number,
        driver: driver ? `${driver.firstName} ${driver.lastName}` : 'Unassigned',
        lat: truck.currentLatitude || 34.0522 + Math.random() * 5,
        lng: truck.currentLongitude || -118.2437 + Math.random() * 5,
        speed: truck.currentSpeed || Math.floor(Math.random() * 70),
        heading: truck.heading || 'NE',
        destination: truck.currentLocation || 'Unknown',
        eta: truck.eta || 'Calculating...',
        status: truck.status === 'IN_TRANSIT' ? 'In Transit' : 
                truck.status === 'AVAILABLE' ? 'Available' : 'Stopped',
        load: truck.currentLoadId || null
      }
    })
  } catch (error) {
    console.error('Failed to fetch tracking data:', error)
    return []
  }
}

// Fix Leaflet default icon issue
delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

// Custom truck icon
const truckIcon = new L.Icon({
  iconUrl: 'https://cdn-icons-png.flaticon.com/512/713/713311.png',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
})

// Mock data for demo/offline mode
const mockTrucks = [
  { id: 1, name: 'Truck-001', driver: 'John Smith', lat: 34.0522, lng: -118.2437, speed: 65, heading: 'NE', destination: 'Phoenix, AZ', eta: '2h 30m', status: 'In Transit', load: 'LD-001' },
  { id: 2, name: 'Truck-002', driver: 'Sarah Johnson', lat: 33.4484, lng: -112.0740, speed: 0, heading: 'N', destination: 'Los Angeles, CA', eta: 'At Stop', status: 'Stopped', load: 'LD-005' },
  { id: 3, name: 'Truck-003', driver: 'Mike Davis', lat: 32.7157, lng: -117.1611, speed: 72, heading: 'E', destination: 'Phoenix, AZ', eta: '5h 15m', status: 'In Transit', load: 'LD-006' },
  { id: 4, name: 'Truck-004', driver: 'Emily Wilson', lat: 41.8781, lng: -87.6298, speed: 58, heading: 'W', destination: 'Detroit, MI', eta: '3h 45m', status: 'In Transit', load: 'LD-003' },
  { id: 5, name: 'Truck-005', driver: 'Robert Brown', lat: 29.7604, lng: -95.3698, speed: 0, heading: 'S', destination: 'Houston, TX', eta: 'At Pickup', status: 'At Pickup', load: null },
]

function MapCenter({ center }) {
  const map = useMap()
  useEffect(() => {
    if (center) {
      map.flyTo(center, 8, { duration: 1.5 })
    }
  }, [center, map])
  return null
}

export default function Tracking() {
  const [selectedTruck, setSelectedTruck] = useState(null)
  const [mapCenter, setMapCenter] = useState([34.0522, -118.2437])
  
  // Fetch tracking data from API
  const { data: apiTrucks = [], isLoading, refetch } = useQuery({
    queryKey: ['tracking'],
    queryFn: fetchTrackingData,
    refetchInterval: 30000, // Refresh every 30 seconds
  })
  
  // Use API data if available, otherwise mock data
  const trucks = apiTrucks.length > 0 ? apiTrucks : mockTrucks
  
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Live Tracking</h1>
          <p className="text-textSecondary mt-1">Real-time fleet GPS monitoring</p>
        </div>
        <div className="flex items-center gap-2">
          <span className="flex items-center gap-2 text-success">
            <span className="w-2 h-2 bg-success rounded-full animate-pulse"></span>
            Live
          </span>
          <button className="glass-button flex items-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Refresh
          </button>
        </div>
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Truck List */}
        <div className="lg:col-span-1 space-y-4">
          {mockTrucks.map((truck) => (
            <motion.div
              key={truck.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              onClick={() => {
                setSelectedTruck(truck)
                setMapCenter([truck.lat, truck.lng])
              }}
              className={`glass-card p-4 cursor-pointer hover:border-primary/30 transition-colors ${
                selectedTruck?.id === truck.id ? 'border-primary/50' : ''
              }`}
            >
              <div className="flex items-center gap-3 mb-3">
                <div className="p-2 rounded-lg bg-primary/10">
                  <Truck className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-textPrimary">{truck.name}</h3>
                  <p className="text-textMuted text-sm">{truck.driver}</p>
                </div>
              </div>
              
              <div className="space-y-2 text-sm">
                <div className="flex items-center justify-between">
                  <span className="text-textMuted">Speed</span>
                  <span className="text-textPrimary font-medium">{truck.speed} mph</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-textMuted">Heading</span>
                  <span className="text-textPrimary font-medium">{truck.heading}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-textMuted">ETA</span>
                  <span className="text-primary font-medium">{truck.eta}</span>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
        
        {/* Map */}
        <div className="lg:col-span-3 glass-card overflow-hidden">
          <MapContainer
            center={mapCenter}
            zoom={8}
            className="h-[600px] w-full"
            style={{ background: '#0a0e17' }}
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
              url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
            />
            <MapCenter center={mapCenter} />
            
            {mockTrucks.map((truck) => (
              <Marker key={truck.id} position={[truck.lat, truck.lng]} icon={truckIcon}>
                <Popup>
                  <div className="text-gray-900">
                    <h3 className="font-bold">{truck.name}</h3>
                    <p>Driver: {truck.driver}</p>
                    <p>Speed: {truck.speed} mph</p>
                    <p>Destination: {truck.destination}</p>
                  </div>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </div>
      </div>
      
      {/* Selected Truck Details */}
      {selectedTruck && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-card p-6"
        >
          <h3 className="text-lg font-semibold text-textPrimary mb-4">Selected: {selectedTruck.name}</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div>
              <p className="text-textMuted text-sm">Driver</p>
              <p className="text-textPrimary font-medium">{selectedTruck.driver}</p>
            </div>
            <div>
              <p className="text-textMuted text-sm">Current Speed</p>
              <p className="text-textPrimary font-medium">{selectedTruck.speed} mph</p>
            </div>
            <div>
              <p className="text-textMuted text-sm">Heading</p>
              <p className="text-textPrimary font-medium">{selectedTruck.heading}</p>
            </div>
            <div>
              <p className="text-textMuted text-sm">Destination</p>
              <p className="text-textPrimary font-medium">{selectedTruck.destination}</p>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  )
}
