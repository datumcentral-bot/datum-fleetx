import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Truck, Users, Route, DollarSign, TrendingUp, Clock, MapPin, AlertTriangle } from 'lucide-react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area } from 'recharts'
import api from '../services/api'
import { useAuthStore } from '../store/authStore'

// Fetch dashboard stats from API
const fetchDashboardStats = async () => {
  try {
    const response = await api.get('/dashboard/stats')
    return response.data.data
  } catch (error) {
    console.error('Failed to fetch dashboard stats:', error)
    return null
  }
}

// Mock data for demo/offline mode
const mockStats = {
  companyName: 'Demo Transport',
  subscriptionPlan: 'TRIAL',
  truckLimit: 5,
  currentTruckCount: 3,
  activeTrucks: 3,
  activeDrivers: 5,
  activeCustomers: 8,
  totalLoads: 24,
  loadStatus: {
    CREATED: 5,
    DISPATCHED: 8,
    IN_TRANSIT: 6,
    DELIVERED: 5
  },
  outstandingAmount: 12500
}

const mockChartData = [
  { name: 'Jan', revenue: 4200 },
  { name: 'Feb', revenue: 3800 },
  { name: 'Mar', revenue: 5100 },
  { name: 'Apr', revenue: 4600 },
  { name: 'May', revenue: 5800 },
  { name: 'Jun', revenue: 6200 },
]

const container = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1
    }
  }
}

const item = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 }
}

function StatCard({ icon: Icon, label, value, subtext, color = 'primary' }) {
  const colorClasses = {
    primary: 'from-primary/20 to-primary/5 border-primary/20',
    accent: 'from-accent/20 to-accent/5 border-accent/20',
    success: 'from-success/20 to-success/5 border-success/20',
    warning: 'from-warning/20 to-warning/5 border-warning/20',
  }
  
  const iconColors = {
    primary: 'text-primary',
    accent: 'text-accent',
    success: 'text-success',
    warning: 'text-warning',
  }
  
  return (
    <motion.div
      variants={item}
      className={`glass-card p-6 bg-gradient-to-br ${colorClasses[color]} border`}
    >
      <div className="flex items-start justify-between">
        <div>
          <p className="text-textMuted text-sm">{label}</p>
          <p className="text-3xl font-bold text-textPrimary mt-2">{value}</p>
          {subtext && <p className="text-textMuted text-xs mt-1">{subtext}</p>}
        </div>
        <div className={`p-3 rounded-xl bg-surface/50 ${iconColors[color]}`}>
          <Icon className="w-6 h-6" />
        </div>
      </div>
    </motion.div>
  )
}

export default function Dashboard() {
  const { user } = useAuthStore()
  
  // Fetch data from API with fallback to mock data
  const { data: apiStats, isLoading, error } = useQuery({
    queryKey: ['dashboard', 'stats'],
    queryFn: fetchDashboardStats,
    retry: 1,
    staleTime: 30000,
  })
  
  // Use API data if available, otherwise use mock data
  const stats = apiStats || mockStats
  
  return (
    <motion.div
      variants={container}
      initial="hidden"
      animate="show"
      className="space-y-6"
    >
      {/* Header */}
      <motion.div variants={item} className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">
            Welcome back, {user?.firstName} 👋
          </h1>
          <p className="text-textSecondary mt-1">
            Here's what's happening with your fleet today
          </p>
        </div>
        <div className="flex items-center gap-3">
          <div className="glass-button flex items-center gap-2">
            <Clock className="w-4 h-4 text-primary" />
            <span className="text-sm">{new Date().toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' })}</span>
          </div>
        </div>
      </motion.div>
      
      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={Truck}
          label="Active Trucks"
          value={stats.activeTrucks}
          subtext={`of ${stats.truckLimit} allowed`}
          color="primary"
        />
        <StatCard
          icon={Users}
          label="Active Drivers"
          value={stats.activeDrivers}
          subtext="Currently on duty"
          color="accent"
        />
        <StatCard
          icon={Route}
          label="Total Loads"
          value={stats.totalLoads}
          subtext="This month"
          color="success"
        />
        <StatCard
          icon={DollarSign}
          label="Outstanding"
          value={`$${stats.outstandingAmount.toLocaleString()}`}
          subtext="Unpaid invoices"
          color="warning"
        />
      </div>
      
      {/* Charts and Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Revenue Chart */}
        <motion.div variants={item} className="lg:col-span-2 glass-card p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-textPrimary">Revenue Overview</h3>
            <div className="flex items-center gap-2 text-success">
              <TrendingUp className="w-4 h-4" />
              <span className="text-sm font-medium">+12.5%</span>
            </div>
          </div>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={mockChartData}>
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#06b6d4" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="name" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#1f2937', 
                    border: '1px solid #374151',
                    borderRadius: '8px'
                  }}
                />
                <Area 
                  type="monotone" 
                  dataKey="revenue" 
                  stroke="#06b6d4" 
                  fillOpacity={1} 
                  fill="url(#colorRevenue)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>
        
        {/* Load Status */}
        <motion.div variants={item} className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-6">Load Status</h3>
          <div className="space-y-4">
            {Object.entries(stats.loadStatus).map(([status, count]) => (
              <div key={status} className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className={`w-3 h-3 rounded-full ${
                    status === 'DELIVERED' ? 'bg-success' :
                    status === 'IN_TRANSIT' ? 'bg-primary' :
                    status === 'DISPATCHED' ? 'bg-warning' :
                    'bg-textMuted'
                  }`} />
                  <span className="text-textSecondary text-sm">{status.replace('_', ' ')}</span>
                </div>
                <span className="text-textPrimary font-semibold">{count}</span>
              </div>
            ))}
          </div>
          
          <div className="mt-6 pt-4 border-t border-surfaceLighter/50">
            <div className="flex items-center justify-between">
              <span className="text-textMuted text-sm">Completion Rate</span>
              <span className="text-success font-semibold">92%</span>
            </div>
          </div>
        </motion.div>
      </div>
      
      {/* Recent Activity & Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Loads */}
        <motion.div variants={item} className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-4">Recent Loads</h3>
          <div className="space-y-3">
            {[
              { id: 'LD-001', customer: 'ABC Logistics', status: 'In Transit', pickup: 'Los Angeles', delivery: 'Phoenix' },
              { id: 'LD-002', customer: 'XYZ Freight', status: 'Delivered', pickup: 'Dallas', delivery: 'Houston' },
              { id: 'LD-003', customer: 'Global Cargo', status: 'Dispatched', pickup: 'Miami', delivery: 'Atlanta' },
            ].map((load) => (
              <div key={load.id} className="flex items-center justify-between p-3 rounded-lg bg-surfaceLight/30 hover:bg-surfaceLight/50 transition-colors">
                <div>
                  <p className="text-textPrimary font-medium">{load.id}</p>
                  <p className="text-textMuted text-sm">{load.customer}</p>
                </div>
                <div className="text-right">
                  <span className={`status-badge ${
                    load.status === 'Delivered' ? 'bg-success/20 text-success' :
                    load.status === 'In Transit' ? 'bg-primary/20 text-primary' :
                    'bg-warning/20 text-warning'
                  }`}>
                    {load.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>
        
        {/* Alerts */}
        <motion.div variants={item} className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-4">Alerts & Notifications</h3>
          <div className="space-y-3">
            {[
              { type: 'warning', message: 'Truck #3 maintenance due in 3 days', icon: AlertTriangle },
              { type: 'info', message: 'New load request from Global Cargo', icon: Route },
              { type: 'success', message: 'Invoice #INV-002 paid by ABC Logistics', icon: DollarSign },
            ].map((alert, index) => (
              <div key={index} className="flex items-start gap-3 p-3 rounded-lg bg-surfaceLight/30">
                <div className={`p-2 rounded-lg ${
                  alert.type === 'warning' ? 'bg-warning/20 text-warning' :
                  alert.type === 'success' ? 'bg-success/20 text-success' :
                  'bg-primary/20 text-primary'
                }`}>
                  <alert.icon className="w-4 h-4" />
                </div>
                <p className="text-textSecondary text-sm">{alert.message}</p>
              </div>
            ))}
          </div>
        </motion.div>
      </div>
      
      {/* Quick Actions */}
      <motion.div variants={item} className="glass-card p-6">
        <h3 className="text-lg font-semibold text-textPrimary mb-4">Quick Actions</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[
            { label: 'Create Load', path: '/loads', color: 'primary' },
            { label: 'Add Truck', path: '/trucks', color: 'accent' },
            { label: 'Add Driver', path: '/drivers', color: 'success' },
            { label: 'View Reports', path: '/dashboard', color: 'warning' },
          ].map((action) => (
            <button
              key={action.label}
              className={`p-4 rounded-xl border border-surfaceLighter/50 hover:border-${action.color}/30 hover:bg-surfaceLight/30 transition-all text-left`}
            >
              <span className="text-textPrimary font-medium">{action.label}</span>
            </button>
          ))}
        </div>
      </motion.div>
    </motion.div>
  )
}
