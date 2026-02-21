import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'
import api from '../services/api'

// Demo user for testing without backend
const DEMO_USER = {
  id: 'demo-001',
  email: 'demo@fleetx.com',
  firstName: 'Demo',
  lastName: 'User',
  role: 'COMPANY_ADMIN',
  companyId: 'demo-company-001',
  companyName: 'Demo Trucking Co.',
  subscriptionPlan: 'PROFESSIONAL'
}

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      
      // Demo login - works without backend (synchronous)
      demoLogin: () => {
        set({
          user: DEMO_USER,
          token: 'demo-token-12345',
          isAuthenticated: true,
          isLoading: false
        })
        return { success: true }
      },
      
      login: async (email, password) => {
        // Check for demo credentials
        if (email === 'demo@fleetx.com' || email === 'demo' || password === 'demo123') {
          return get().demoLogin()
        }
        
        set({ isLoading: true })
        try {
          const response = await api.post('/api/v1/auth/login', { email, password })
          const { token, userId, firstName, lastName, role, companyId, companyName, subscriptionPlan } = response.data.data
          
          set({
            user: {
              id: userId,
              email,
              firstName,
              lastName,
              role,
              companyId,
              companyName,
              subscriptionPlan
            },
            token,
            isAuthenticated: true,
            isLoading: false
          })
          
          return { success: true }
        } catch (error) {
          set({ isLoading: false })
          return { 
            success: false, 
            message: error.response?.data?.message || 'Login failed' 
          }
        }
      },
      
      register: async (data) => {
        // Demo mode - auto-create account
        const demoUser = {
          ...data,
          id: 'demo-' + Date.now(),
          role: 'COMPANY_OWNER',
          companyId: 'demo-company-' + Date.now(),
          companyName: data.companyName || 'Demo Transport Co.',
          subscriptionPlan: 'TRIAL'
        }
        
        set({
          user: demoUser,
          token: 'demo-token-' + Date.now(),
          isAuthenticated: true,
          isLoading: false
        })
        
        return { success: true }
      },
      
      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false
        })
      },
      
      updateUser: (userData) => {
        set((state) => ({
          user: { ...state.user, ...userData }
        }))
      }
    }),
    {
      name: 'fleetx-auth',
      partialize: (state) => ({ 
        user: state.user, 
        token: state.token, 
        isAuthenticated: state.isAuthenticated 
      }),
    }
  )
)
