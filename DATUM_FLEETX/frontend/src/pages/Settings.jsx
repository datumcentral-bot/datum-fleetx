import { useState } from 'react'
import { motion } from 'framer-motion'
import { Settings, User, Building, Bell, Shield, CreditCard, Globe, Palette, Save, Key, Mail, Phone, MapPin } from 'lucide-react'

const tabs = [
  { id: 'profile', label: 'Profile', icon: User },
  { id: 'company', label: 'Company', icon: Building },
  { id: 'notifications', label: 'Notifications', icon: Bell },
  { id: 'security', label: 'Security', icon: Shield },
  { id: 'billing', label: 'Billing', icon: CreditCard },
]

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState('profile')
  
  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-textPrimary">Settings</h1>
        <p className="text-textSecondary mt-1">Manage your account and preferences</p>
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Sidebar */}
        <div className="lg:col-span-1">
          <div className="glass-card p-4">
            <nav className="space-y-1">
              {tabs.map((tab) => {
                const Icon = tab.icon
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors ${
                      activeTab === tab.id
                        ? 'bg-primary/10 text-primary'
                        : 'text-textSecondary hover:bg-surfaceLight hover:text-textPrimary'
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                    {tab.label}
                  </button>
                )
              })}
            </nav>
          </div>
        </div>
        
        {/* Content */}
        <div className="lg:col-span-3">
          {activeTab === 'profile' && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6"
            >
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Profile Settings</h2>
              
              <div className="space-y-6">
                <div className="flex items-center gap-6">
                  <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white text-2xl font-bold">
                    DU
                  </div>
                  <div>
                    <button className="btn-primary py-2 px-4 text-sm">Change Photo</button>
                    <p className="text-textMuted text-sm mt-2">JPG, PNG. Max 2MB</p>
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">First Name</label>
                    <input type="text" defaultValue="Demo" className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Last Name</label>
                    <input type="text" defaultValue="User" className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Email</label>
                    <input type="email" defaultValue="demo@fleetx.com" className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Phone</label>
                    <input type="tel" defaultValue="+1 555 000 0000" className="input-field" />
                  </div>
                </div>
                
                <button className="btn-primary flex items-center gap-2">
                  <Save className="w-4 h-4" />
                  Save Changes
                </button>
              </div>
            </motion.div>
          )}
          
          {activeTab === 'company' && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6"
            >
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Company Settings</h2>
              
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Company Name</label>
                    <input type="text" defaultValue="Demo Trucking Co." className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Business Phone</label>
                    <input type="tel" defaultValue="+1 555 000 0000" className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Country</label>
                    <select className="input-field">
                      <option value="US">United States</option>
                      <option value="CA">Canada</option>
                      <option value="GB">United Kingdom</option>
                      <option value="AU">Australia</option>
                      <option value="AE">United Arab Emirates</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Timezone</label>
                    <select className="input-field">
                      <option value="America/New_York">Eastern Time</option>
                      <option value="America/Chicago">Central Time</option>
                      <option value="America/Denver">Mountain Time</option>
                      <option value="America/Los_Angeles">Pacific Time</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm text-textSecondary mb-2">Base Currency</label>
                    <select className="input-field">
                      <option value="USD">USD ($)</option>
                      <option value="CAD">CAD ($)</option>
                      <option value="GBP">GBP (£)</option>
                      <option value="AUD">AUD ($)</option>
                      <option value="AED">AED (د.إ)</option>
                    </select>
                  </div>
                </div>
                
                <button className="btn-primary flex items-center gap-2">
                  <Save className="w-4 h-4" />
                  Save Changes
                </button>
              </div>
            </motion.div>
          )}
          
          {activeTab === 'notifications' && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6"
            >
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Notification Preferences</h2>
              
              <div className="space-y-4">
                {[
                  { label: 'Email notifications for new loads', enabled: true },
                  { label: 'Push notifications for dispatch updates', enabled: true },
                  { label: 'Daily fleet summary reports', enabled: false },
                  { label: 'Invoice payment reminders', enabled: true },
                  { label: 'Driver status alerts', enabled: true },
                  { label: 'Marketing and product updates', enabled: false },
                ].map((item, index) => (
                  <div key={index} className="flex items-center justify-between p-4 bg-surfaceLight/30 rounded-xl">
                    <span className="text-textSecondary">{item.label}</span>
                    <button
                      className={`w-12 h-6 rounded-full transition-colors ${
                        item.enabled ? 'bg-primary' : 'bg-surfaceLighter'
                      }`}
                    >
                      <div
                        className={`w-5 h-5 rounded-full bg-white shadow-md transform transition-transform ${
                          item.enabled ? 'translate-x-6' : 'translate-x-0.5'
                        }`}
                      />
                    </button>
                  </div>
                ))}
              </div>
            </motion.div>
          )}
          
          {activeTab === 'security' && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6"
            >
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Security Settings</h2>
              
              <div className="space-y-6">
                <div className="p-4 bg-surfaceLight/30 rounded-xl">
                  <h3 className="font-medium text-textPrimary mb-4">Change Password</h3>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm text-textSecondary mb-2">Current Password</label>
                      <input type="password" className="input-field" />
                    </div>
                    <div>
                      <label className="block text-sm text-textSecondary mb-2">New Password</label>
                      <input type="password" className="input-field" />
                    </div>
                    <div>
                      <label className="block text-sm text-textSecondary mb-2">Confirm New Password</label>
                      <input type="password" className="input-field" />
                    </div>
                    <button className="btn-primary">Update Password</button>
                  </div>
                </div>
                
                <div className="p-4 bg-surfaceLight/30 rounded-xl">
                  <h3 className="font-medium text-textPrimary mb-4">Two-Factor Authentication</h3>
                  <p className="text-textSecondary text-sm mb-4">Add an extra layer of security to your account</p>
                  <button className="btn-primary">Enable 2FA</button>
                </div>
              </div>
            </motion.div>
          )}
          
          {activeTab === 'billing' && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6"
            >
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Billing & Subscription</h2>
              
              <div className="space-y-6">
                <div className="p-4 bg-gradient-to-r from-primary/10 to-accent/10 rounded-xl border border-primary/20">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-textMuted text-sm">Current Plan</p>
                      <p className="text-2xl font-bold text-textPrimary">Professional</p>
                      <p className="text-textSecondary text-sm">Up to 25 trucks • $149/month</p>
                    </div>
                    <button className="btn-primary">Upgrade Plan</button>
                  </div>
                </div>
                
                <div>
                  <h3 className="font-medium text-textPrimary mb-4">Payment Method</h3>
                  <div className="p-4 bg-surfaceLight/30 rounded-xl flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <CreditCard className="w-8 h-8 text-textMuted" />
                      <div>
                        <p className="text-textPrimary">•••• •••• •••• 4242</p>
                        <p className="text-textMuted text-sm">Expires 12/2027</p>
                      </div>
                    </div>
                    <button className="text-primary hover:underline">Edit</button>
                  </div>
                </div>
                
                <div>
                  <h3 className="font-medium text-textPrimary mb-4">Billing History</h3>
                  <div className="space-y-2">
                    {[
                      { date: 'Feb 1, 2026', amount: '$149.00', status: 'Paid' },
                      { date: 'Jan 1, 2026', amount: '$149.00', status: 'Paid' },
                      { date: 'Dec 1, 2025', amount: '$149.00', status: 'Paid' },
                    ].map((item, index) => (
                      <div key={index} className="flex items-center justify-between p-3 bg-surfaceLight/30 rounded-xl">
                        <span className="text-textSecondary">{item.date}</span>
                        <span className="text-textPrimary">{item.amount}</span>
                        <span className="text-success text-sm">{item.status}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </motion.div>
          )}
        </div>
      </div>
    </div>
  )
}
