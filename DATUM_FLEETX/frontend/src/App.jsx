import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'
import Layout from './components/Layout'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Loads from './pages/Loads'
import Trucks from './pages/Trucks'
import Drivers from './pages/Drivers'
import Customers from './pages/Customers'
import DispatchBoard from './pages/DispatchBoard'
import Tracking from './pages/Tracking'
import Invoices from './pages/Invoices'
import Settings from './pages/Settings'
import Reports from './pages/Reports'
import PublicTracking from './pages/PublicTracking'
import { useState, useEffect } from 'react'

function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuthStore()
  const [loading, setLoading] = useState(true)
  const [isAuth, setIsAuth] = useState(false)
  
  useEffect(() => {
    // Check localStorage directly for persisted auth
    const stored = localStorage.getItem('fleetx-auth')
    if (stored) {
      try {
        const parsed = JSON.parse(stored)
        if (parsed.state && parsed.state.isAuthenticated) {
          setIsAuth(true)
        }
      } catch (e) {
        console.log('Auth parse error', e)
      }
    }
    setLoading(false)
  }, [])
  
  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-primary">Loading...</div>
      </div>
    )
  }
  
  if (!isAuthenticated && !isAuth) {
    return <Navigate to="/login" replace />
  }
  
  return <Layout>{children}</Layout>
}

function PublicRoute({ children }) {
  const { isAuthenticated } = useAuthStore()
  
  // Also check localStorage
  const stored = localStorage.getItem('fleetx-auth')
  let storedAuth = false
  if (stored) {
    try {
      const parsed = JSON.parse(stored)
      storedAuth = parsed.state && parsed.state.isAuthenticated
    } catch (e) {}
  }
  
  if (isAuthenticated || storedAuth) {
    return <Navigate to="/dashboard" replace />
  }
  
  return children
}

export default function App() {
  return (
    <Routes>
      {/* Public routes */}
      <Route 
        path="/login" 
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        } 
      />
      <Route 
        path="/register" 
        element={
          <PublicRoute>
            <Register />
          </PublicRoute>
        } 
      />
      
      {/* Public tracking - no auth required */}
      <Route path="/track" element={<PublicTracking />} />
      <Route path="/track/:code" element={<PublicTracking />} />
      
      {/* Protected routes */}
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      <Route path="/loads" element={<ProtectedRoute><Loads /></ProtectedRoute>} />
      <Route path="/trucks" element={<ProtectedRoute><Trucks /></ProtectedRoute>} />
      <Route path="/drivers" element={<ProtectedRoute><Drivers /></ProtectedRoute>} />
      <Route path="/customers" element={<ProtectedRoute><Customers /></ProtectedRoute>} />
      <Route path="/dispatch" element={<ProtectedRoute><DispatchBoard /></ProtectedRoute>} />
      <Route path="/tracking" element={<ProtectedRoute><Tracking /></ProtectedRoute>} />
      <Route path="/invoices" element={<ProtectedRoute><Invoices /></ProtectedRoute>} />
      <Route path="/settings" element={<ProtectedRoute><Settings /></ProtectedRoute>} />
      <Route path="/reports" element={<ProtectedRoute><Reports /></ProtectedRoute>} />
      
      {/* 404 */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
