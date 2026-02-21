import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Truck, Mail, Lock, Eye, EyeOff, AlertCircle } from 'lucide-react'
import { useAuthStore } from '../store/authStore'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  
  const { login, demoLogin, isLoading } = useAuthStore()
  const navigate = useNavigate()
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    
    if (!email || !password) {
      setError('Please enter email and password')
      return
    }
    
    // Use demo login from store
    const result = await demoLogin()
    if (result.success) {
      navigate('/dashboard')
    }
  }
  
  const handleDemoLogin = () => {
    // Directly call demo login and navigate
    demoLogin().then(() => {
      navigate('/dashboard')
    })
  }
  
  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      {/* Background effects */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-1/4 -left-32 w-96 h-96 bg-primary/10 rounded-full blur-3xl" />
        <div className="absolute bottom-1/4 -right-32 w-96 h-96 bg-accent/10 rounded-full blur-3xl" />
      </div>
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-md"
      >
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-primary to-accent mb-4">
            <Truck className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gradient">Datum FleetX</h1>
          <p className="text-textSecondary mt-2">Enterprise Truck Dispatch Platform</p>
        </div>
        
        {/* Login form */}
        <div className="glass-card p-8">
          <h2 className="text-2xl font-bold text-textPrimary mb-6">Welcome Back</h2>
          
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex items-center gap-2 p-3 mb-4 rounded-lg bg-danger/10 border border-danger/20 text-danger"
            >
              <AlertCircle className="w-5 h-5" />
              <span className="text-sm">{error}</span>
            </motion.div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-textSecondary mb-2">Email</label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="input-field pl-10"
                  placeholder="demo@fleetx.com"
                  required
                />
              </div>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-textSecondary mb-2">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-textMuted" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="input-field pl-10 pr-10"
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-textMuted hover:text-textPrimary"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>
            
            <button
              type="submit"
              disabled={isLoading}
              className="w-full btn-primary py-3.5"
            >
              {isLoading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin w-5 h-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Signing in...
                </span>
              ) : (
                'Sign In'
              )}
            </button>
          </form>
          
          {/* Demo Login Button */}
          <button
            type="button"
            onClick={handleDemoLogin}
            className="w-full mt-4 py-3 px-4 rounded-xl font-semibold transition-all duration-300
                   bg-gradient-to-r from-primary/20 to-accent/20 border border-primary/30
                   text-primary hover:text-primary-light hover:border-primary/50 hover:shadow-glow-cyan"
          >
            🚀 Try Demo Mode (No Backend Required)
          </button>
          
          <div className="mt-6 text-center">
            <p className="text-textSecondary">
              Don't have an account?{' '}
              <Link to="/register" className="text-primary hover:text-primary-light font-medium">
                Start Free Trial
              </Link>
            </p>
          </div>
        </div>
        
        {/* Features preview */}
        <div className="mt-8 grid grid-cols-3 gap-4">
          {[
            { label: 'Multi-tenant', value: '✓' },
            { label: 'Live Tracking', value: '✓' },
            { label: 'AI Powered', value: '✓' },
          ].map((feature) => (
            <div key={feature.label} className="text-center p-3 rounded-xl bg-surfaceLight/30">
              <p className="text-2xl font-bold text-primary">{feature.value}</p>
              <p className="text-xs text-textMuted">{feature.label}</p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  )
}
