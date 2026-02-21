import { useState } from 'react'
import { motion } from 'framer-motion'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import L from 'leaflet'
import { Search, Package, Truck, MapPin, Clock, Phone, CheckCircle, Circle, AlertCircle } from 'lucide-react'
import api from '../services/api'

// Fix Leaflet default icon issue
delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

// Custom icons
const truckIcon = new L.Icon({
  iconUrl: 'https://cdn-icons-png.flaticon.com/512/713/713311.png',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
})

const locationIcon = new L.Icon({
  iconUrl: 'https://cdn-icons-png.flaticon.com/512/535/535239.png',
  iconSize: [24, 24],
  iconAnchor: [12, 24],
})

export default function PublicTracking() {
  const [trackingCode, setTrackingCode] = useState('')
  const [trackingData, setTrackingData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleTrack = async () => {
    if (!trackingCode.trim()) return
    
    setLoading(true)
    setError('')
    setTrackingData(null)
    
    try {
      const response = await api.get(`/public/track/${trackingCode}`)
      if (response.data.success) {
        setTrackingData(response.data.data)
      } else {
        setError(response.data.message || 'Tracking code not found')
      }
    } catch (err) {
      setError('Failed to track shipment. Please check the tracking code and try again.')
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'DELIVERED': return 'text-success'
      case 'IN_TRANSIT': return 'text-primary'
      case 'PICKED_UP': return 'text-accent'
      case 'CANCELLED': return 'text-danger'
      default: return 'text-warning'
    }
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case 'DELIVERED': return <CheckCircle className="w-5 h-5 text-success" />
      case 'IN_TRANSIT': return <Truck className="w-5 h-5 text-primary" />
      default: return <Circle className="w-5 h-5 text-warning" />
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Header */}
      <div className="glass-card border-b border-slate-700/50">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-cyan-500 to-purple-600 flex items-center justify-center">
                <Truck className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-white">Datum FleetX</h1>
                <p className="text-sm text-slate-400">Track Your Shipment</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-12">
        {/* Search Section */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-card p-8 mb-8"
        >
          <h2 className="text-2xl font-bold text-white mb-6 text-center">
            Track Your Shipment
          </h2>
          
          <div className="flex gap-4 max-w-2xl mx-auto">
            <div className="flex-1 relative">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
              <input
                type="text"
                value={trackingCode}
                onChange={(e) => setTrackingCode(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleTrack()}
                placeholder="Enter tracking number (e.g., LD-001)"
                className="w-full pl-12 pr-4 py-4 bg-slate-800/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
              />
            </div>
            <button
              onClick={handleTrack}
              disabled={loading}
              className="px-8 py-4 bg-gradient-to-r from-cyan-500 to-purple-600 text-white font-semibold rounded-xl hover:from-cyan-400 hover:to-purple-500 transition-all disabled:opacity-50"
            >
              {loading ? 'Tracking...' : 'Track'}
            </button>
          </div>

          {error && (
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="mt-4 p-4 bg-danger/20 border border-danger/30 rounded-xl text-center text-danger"
            >
              {error}
            </motion.div>
          )}
        </motion.div>

        {/* Tracking Results */}
        {trackingData && (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-6"
          >
            {/* Status Card */}
            <div className="glass-card p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-xl font-bold text-white">{trackingData.loadNumber}</h3>
                  {trackingData.referenceNumber && (
                    <p className="text-slate-400">Ref: {trackingData.referenceNumber}</p>
                  )}
                </div>
                <div className={`px-4 py-2 rounded-full bg-slate-800 ${getStatusColor(trackingData.status)}`}>
                  {getStatusIcon(trackingData.status)}
                  <span className="ml-2 font-semibold">{trackingData.status?.replace('_', ' ')}</span>
                </div>
              </div>

              {/* Progress Bar */}
              <div className="mb-6">
                <div className="h-3 bg-slate-700 rounded-full overflow-hidden">
                  <motion.div 
                    initial={{ width: 0 }}
                    animate={{ width: `${trackingData.progress}%` }}
                    transition={{ duration: 1 }}
                    className="h-full bg-gradient-to-r from-cyan-500 to-purple-600"
                  />
                </div>
                <div className="flex justify-between mt-2 text-sm text-slate-400">
                  <span>Created</span>
                  <span>In Transit</span>
                  <span>Delivered</span>
                </div>
              </div>

              {/* Route Info */}
              <div className="grid md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-success/20 flex items-center justify-center">
                      <MapPin className="w-4 h-4 text-success" />
                    </div>
                    <div>
                      <p className="text-sm text-slate-400">Pickup</p>
                      <p className="text-white font-medium">{trackingData.pickupLocation || 'N/A'}</p>
                      {trackingData.pickupDate && (
                        <p className="text-sm text-slate-400">{new Date(trackingData.pickupDate).toLocaleDateString()}</p>
                      )}
                    </div>
                  </div>
                  
                  <div className="flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center">
                      <MapPin className="w-4 h-4 text-primary" />
                    </div>
                    <div>
                      <p className="text-sm text-slate-400">Delivery</p>
                      <p className="text-white font-medium">{trackingData.deliveryLocation || 'N/A'}</p>
                      {trackingData.deliveryDate && (
                        <p className="text-sm text-slate-400">{new Date(trackingData.deliveryDate).toLocaleDateString()}</p>
                      )}
                    </div>
                  </div>
                </div>

                <div className="space-y-4">
                  {trackingData.driver && (
                    <div className="flex items-start gap-3">
                      <div className="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center">
                        <Phone className="w-4 h-4 text-accent" />
                      </div>
                      <div>
                        <p className="text-sm text-slate-400">Driver</p>
                        <p className="text-white font-medium">{trackingData.driver.name}</p>
                        <a href={`tel:${trackingData.driver.phone}`} className="text-sm text-cyan-400 hover:underline">
                          {trackingData.driver.phone}
                        </a>
                      </div>
                    </div>
                  )}

                  {trackingData.estimatedArrival && (
                    <div className="flex items-start gap-3">
                      <div className="w-8 h-8 rounded-full bg-warning/20 flex items-center justify-center">
                        <Clock className="w-4 h-4 text-warning" />
                      </div>
                      <div>
                        <p className="text-sm text-slate-400">ETA</p>
                        <p className="text-white font-medium">
                          {new Date(trackingData.estimatedArrival).toLocaleString()}
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Map */}
            {trackingData.truck && trackingData.truck.currentLatitude && (
              <div className="glass-card p-6">
                <h3 className="text-lg font-bold text-white mb-4">Live Location</h3>
                <div className="h-80 rounded-xl overflow-hidden">
                  <MapContainer
                    center={[trackingData.truck.currentLatitude, trackingData.truck.currentLongitude]}
                    zoom={10}
                    style={{ height: '100%', width: '100%' }}
                  >
                    <TileLayer
                      attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    <Marker 
                      position={[trackingData.truck.currentLatitude, trackingData.truck.currentLongitude]}
                      icon={truckIcon}
                    >
                      <Popup>
                        <div className="text-slate-900">
                          <p className="font-bold">{trackingData.loadNumber}</p>
                          <p className="text-sm">Last updated: {new Date(trackingData.truck.lastUpdate).toLocaleString()}</p>
                        </div>
                      </Popup>
                    </Marker>
                  </MapContainer>
                </div>
              </div>
            )}
          </motion.div>
        )}

        {/* How it works */}
        {!trackingData && !loading && (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center mt-12"
          >
            <h3 className="text-xl font-bold text-white mb-6">How to Track Your Shipment</h3>
            <div className="grid md:grid-cols-3 gap-6">
              <div className="glass-card p-6">
                <div className="w-12 h-12 rounded-full bg-cyan-500/20 flex items-center justify-center mx-auto mb-4">
                  <span className="text-2xl font-bold text-cyan-400">1</span>
                </div>
                <h4 className="text-white font-semibold mb-2">Get Tracking Number</h4>
                <p className="text-slate-400 text-sm">Your tracking number is provided in your shipping confirmation email</p>
              </div>
              <div className="glass-card p-6">
                <div className="w-12 h-12 rounded-full bg-purple-500/20 flex items-center justify-center mx-auto mb-4">
                  <span className="text-2xl font-bold text-purple-400">2</span>
                </div>
                <h4 className="text-white font-semibold mb-2">Enter Tracking Code</h4>
                <p className="text-slate-400 text-sm">Type your tracking number in the search box above</p>
              </div>
              <div className="glass-card p-6">
                <div className="w-12 h-12 rounded-full bg-pink-500/20 flex items-center justify-center mx-auto mb-4">
                  <span className="text-2xl font-bold text-pink-400">3</span>
                </div>
                <h4 className="text-white font-semibold mb-2">View Live Status</h4>
                <p className="text-slate-400 text-sm">See real-time location and delivery progress</p>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  )
}
