import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell } from 'recharts'
import { TrendingUp, Truck, DollarSign, Users, Route, Clock, Fuel, AlertTriangle, Download, Calendar } from 'lucide-react'
import api from '../services/api'

// Fetch reports data from API
const fetchReportsData = async () => {
  try {
    const response = await api.get('/dashboard/stats')
    return response.data.data || null
  } catch (error) {
    console.error('Failed to fetch reports data:', error)
    return null
  }
}

// Mock data for demo/offline mode
const revenueData = [
  { month: 'Jan', revenue: 42000, expenses: 28000 },
  { month: 'Feb', revenue: 38000, expenses: 25000 },
  { month: 'Mar', revenue: 51000, expenses: 32000 },
  { month: 'Apr', revenue: 46000, expenses: 29000 },
  { month: 'May', revenue: 58000, expenses: 35000 },
  { month: 'Jun', revenue: 62000, expenses: 38000 },
]

const fleetUtilization = [
  { name: 'Available', value: 35, color: '#22c55e' },
  { name: 'In Transit', value: 45, color: '#06b6d4' },
  { name: 'Maintenance', value: 15, color: '#f59e0b' },
  { name: 'Idle', value: 5, color: '#6b7280' },
]

const driverPerformance = [
  { name: 'John S.', deliveries: 45, onTime: 98, rating: 4.9 },
  { name: 'Sarah J.', deliveries: 42, onTime: 100, rating: 5.0 },
  { name: 'Mike D.', deliveries: 38, onTime: 95, rating: 4.7 },
  { name: 'Emily W.', deliveries: 35, onTime: 99, rating: 4.8 },
  { name: 'Robert B.', deliveries: 32, onTime: 97, rating: 4.6 },
]

const revenueByCustomer = [
  { customer: 'Global Cargo', revenue: 245000 },
  { customer: 'Premier', revenue: 189000 },
  { customer: 'FastShip', revenue: 156000 },
  { customer: 'ABC', revenue: 125000 },
  { customer: 'XYZ', revenue: 98000 },
]

export default function Reports() {
  const [dateRange, setDateRange] = useState('6months')
  
  // Fetch reports data from API
  const { data: apiData } = useQuery({
    queryKey: ['reports'],
    queryFn: fetchReportsData,
  })
  
  // Use API data if available to override mock stats
  const stats = apiData ? [
    { label: 'Total Revenue', value: `${apiData.totalRevenue?.toLocaleString() || 0}`, change: '+12.5%', icon: DollarSign, color: 'text-success' },
    { label: 'Active Loads', value: apiData.activeLoads?.toString() || '0', change: '+5.2%', icon: Route, color: 'text-warning' },
    { label: 'Available Trucks', value: apiData.availableTrucks?.toString() || '0', change: '+18.3%', icon: Truck, color: 'text-primary' },
    { label: 'Active Drivers', value: apiData.activeDrivers?.toString() || '0', change: '+8.7%', icon: Users, color: 'text-accent' },
  ] : [
    { label: 'Total Revenue', value: '$297,000', change: '+12.5%', icon: DollarSign, color: 'text-success' },
    { label: 'Total Expenses', value: '$187,000', change: '+5.2%', icon: TrendingUp, color: 'text-warning' },
    { label: 'Net Profit', value: '$110,000', change: '+18.3%', icon: TrendingUp, color: 'text-primary' },
    { label: 'Avg Revenue/Truck', value: '$18,312', change: '+8.7%', icon: Truck, color: 'text-accent' },
  ]
  
  const kpis = [
    { label: 'Fleet Utilization', value: '85%', target: '90%', status: 'good' },
    { label: 'On-Time Delivery', value: '97.2%', target: '95%', status: 'good' },
    { label: 'Avg Fuel Efficiency', value: '7.1 MPG', target: '7.0 MPG', status: 'good' },
    { label: 'Revenue per Mile', value: '$2.45', target: '$2.50', status: 'warning' },
  ]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-textPrimary">Reports & Analytics</h1>
          <p className="text-textSecondary mt-1">Business insights and performance metrics</p>
        </div>
        <div className="flex items-center gap-3">
          <select 
            value={dateRange}
            onChange={(e) => setDateRange(e.target.value)}
            className="input-field"
          >
            <option value="30days">Last 30 Days</option>
            <option value="3months">Last 3 Months</option>
            <option value="6months">Last 6 Months</option>
            <option value="12months">Last 12 Months</option>
          </select>
          <button className="btn-primary flex items-center gap-2">
            <Download className="w-4 h-4" />
            Export
          </button>
        </div>
      </div>
      
      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="glass-card p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-xl bg-surfaceLight/50 ${stat.color}`}>
                  <Icon className="w-6 h-6" />
                </div>
                <span className="text-success text-sm font-medium">{stat.change}</span>
              </div>
              <p className="text-textMuted text-sm">{stat.label}</p>
              <p className="text-2xl font-bold text-textPrimary">{stat.value}</p>
            </motion.div>
          )
        })}
      </div>
      
      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-6">Revenue vs Expenses</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="month" stroke="#9CA3AF" />
              <YAxis stroke="#9CA3AF" />
              <Tooltip 
                contentStyle={{ backgroundColor: '#1F2937', border: 'none', borderRadius: '8px' }}
                labelStyle={{ color: '#fff' }}
              />
              <Bar dataKey="revenue" fill="#06b6d4" name="Revenue" radius={[4, 4, 0, 0]} />
              <Bar dataKey="expenses" fill="#8b5cf6" name="Expenses" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        
        {/* Fleet Utilization */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-6">Fleet Utilization</h3>
          <div className="flex items-center justify-center">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={fleetUtilization}
                  cx="50%"
                  cy="50%"
                  innerRadius={80}
                  outerRadius={120}
                  paddingAngle={2}
                  dataKey="value"
                >
                  {fleetUtilization.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1F2937', border: 'none', borderRadius: '8px' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="flex flex-wrap justify-center gap-4 mt-4">
            {fleetUtilization.map((item) => (
              <div key={item.name} className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }} />
                <span className="text-textSecondary text-sm">{item.name} {item.value}%</span>
              </div>
            ))}
          </div>
        </div>
      </div>
      
      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Trend */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-6">Revenue Trend</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="month" stroke="#9CA3AF" />
              <YAxis stroke="#9CA3AF" />
              <Tooltip 
                contentStyle={{ backgroundColor: '#1F2937', border: 'none', borderRadius: '8px' }}
              />
              <Line type="monotone" dataKey="revenue" stroke="#22c55e" strokeWidth={3} dot={{ fill: '#22c55e' }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
        
        {/* Revenue by Customer */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold text-textPrimary mb-6">Top Customers by Revenue</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={revenueByCustomer} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis type="number" stroke="#9CA3AF" />
              <YAxis dataKey="customer" type="category" stroke="#9CA3AF" width={100} />
              <Tooltip 
                contentStyle={{ backgroundColor: '#1F2937', border: 'none', borderRadius: '8px' }}
              />
              <Bar dataKey="revenue" fill="#8b5cf6" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
      
      {/* KPIs */}
      <div className="glass-card p-6">
        <h3 className="text-lg font-semibold text-textPrimary mb-6">Performance KPIs</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {kpis.map((kpi) => (
            <div key={kpi.label} className="p-4 bg-surfaceLight/30 rounded-xl">
              <div className="flex items-center justify-between mb-2">
                <span className="text-textSecondary text-sm">{kpi.label}</span>
                {kpi.status === 'good' ? (
                  <span className="text-success text-xs">On Track</span>
                ) : (
                  <span className="text-warning text-xs flex items-center gap-1">
                    <AlertTriangle className="w-3 h-3" />
                    Needs Attention
                  </span>
                )}
              </div>
              <div className="flex items-end gap-2">
                <span className="text-2xl font-bold text-textPrimary">{kpi.value}</span>
                <span className="text-textMuted text-sm mb-1">/ {kpi.target}</span>
              </div>
              <div className="mt-2 h-2 bg-surfaceLighter rounded-full overflow-hidden">
                <div 
                  className={`h-full rounded-full ${kpi.status === 'good' ? 'bg-success' : 'bg-warning'}`}
                  style={{ width: `${(parseFloat(kpi.value) / parseFloat(kpi.target)) * 100}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>
      
      {/* Driver Performance Table */}
      <div className="glass-card p-6">
        <h3 className="text-lg font-semibold text-textPrimary mb-6">Driver Performance</h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-surfaceLighter/50">
                <th className="text-left p-3 text-textMuted font-medium">Driver</th>
                <th className="text-left p-3 text-textMuted font-medium">Deliveries</th>
                <th className="text-left p-3 text-textMuted font-medium">On-Time %</th>
                <th className="text-left p-3 text-textMuted font-medium">Rating</th>
                <th className="text-left p-3 text-textMuted font-medium">Performance</th>
              </tr>
            </thead>
            <tbody>
              {driverPerformance.map((driver) => (
                <tr key={driver.name} className="border-b border-surfaceLighter/30">
                  <td className="p-3 font-medium text-textPrimary">{driver.name}</td>
                  <td className="p-3 text-textSecondary">{driver.deliveries}</td>
                  <td className="p-3">
                    <span className={driver.onTime >= 98 ? 'text-success' : 'text-warning'}>
                      {driver.onTime}%
                    </span>
                  </td>
                  <td className="p-3 text-textSecondary">{driver.rating}</td>
                  <td className="p-3">
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-2 bg-surfaceLighter rounded-full overflow-hidden">
                        <div 
                          className="h-full bg-primary rounded-full"
                          style={{ width: `${driver.rating * 20}%` }}
                        />
                      </div>
                      <span className="text-textMuted text-xs">{driver.rating * 20}%</span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
