# 🚛 Datum FleetX - Enterprise Truck Dispatch Platform

A full-featured, enterprise-grade truck dispatch SaaS platform built with Java Spring Boot and React. Perfect for trucking companies in USA, Canada, UK, Australia, and Middle East.

## 🌟 Features

### Core Features
- **Multi-Tenant SaaS Architecture** - Company-based data isolation
- **Advanced Dispatch Board** - Kanban-style drag & drop load management
- **Live GPS Tracking** - Real-time truck tracking with OpenStreetMap (free)
- **Fleet Management** - Trucks, Drivers, Customers management
- **Financial Engine** - Invoicing, payments, revenue tracking
- **AI-Powered Analytics** - Revenue forecasting, performance metrics

### Target Markets
- 🇺🇸 USA
- 🇨🇦 Canada
- 🇬🇧 UK
- 🇦🇺 Australia
- 🇦🇪 Middle East

### Pricing Model (Per Truck SaaS)
- **Starter**: $49/month (up to 5 trucks)
- **Professional**: $149/month (up to 25 trucks)
- **Enterprise**: Custom pricing

## 🛠 Tech Stack

### Backend
- Java 11+ with Spring Boot 2.7
- PostgreSQL Database
- JWT Authentication
- RESTful APIs

### Frontend
- React 18 + Vite
- TailwindCSS (Futuristic Dark UI)
- Zustand (State Management)
- React Query (API Handling)
- Leaflet + OpenStreetMap (Free GPS)
- Framer Motion (Animations)

### Infrastructure (Free Tier Ready)
- Frontend: Vercel / Netlify
- Backend: Render / Railway
- Database: Neon PostgreSQL / Supabase
- Maps: OpenStreetMap (Free)

## 📁 Project Structure

```
datum-fleetx/
├── backend/                 # Java Spring Boot API
│   ├── src/main/java/com/datum/fleetx/
│   │   ├── config/         # Security, CORS, Database config
│   │   ├── controller/     # REST API endpoints
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── entity/        # JPA Entities
│   │   ├── repository/    # Data Access Layer
│   │   └── security/      # JWT Authentication
│   └── pom.xml
│
├── frontend/               # React + Vite App
│   ├── src/
│   │   ├── components/    # Layout, UI Components
│   │   ├── pages/        # Dashboard, Trucks, Drivers, etc.
│   │   ├── services/     # API Service
│   │   └── store/        # Zustand State
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- Java 11+
- PostgreSQL (or use Neon free tier)

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on: http://localhost:8080

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: http://localhost:5173

### Demo Mode
The frontend works without backend using demo data:
1. Open http://localhost:5173
2. Click "Use Demo Credentials" or just click "Sign In"
3. Explore all features with sample data

## 🔐 Security Features

- JWT Token Authentication
- Role-Based Access Control (RBAC)
- Company-based Data Isolation
- Password Encryption (BCrypt)
- CORS Configuration

## 📱 Mobile Ready

The app is responsive and PWA-ready. Works on:
- Desktop
- Tablet
- Mobile

## 🌍 International Support

- Multi-currency (USD, CAD, GBP, AUD, AED)
- Multi-timezone
- Country-specific compliance presets
- Multi-language ready

## 📊 Key Pages

| Page | Description |
|------|-------------|
| Dashboard | KPIs, Revenue Charts, Fleet Overview |
| Trucks | Fleet management with search & filters |
| Drivers | Driver roster with safety scores |
| Customers | Shipper management with credit tracking |
| Loads | Load management & tracking |
| Dispatch Board | Kanban-style load assignment |
| Tracking | Live GPS map with truck positions |

## 💰 Revenue Model

This platform can generate:
- $147/month with 3 customers ($49×3)
- $9,950/month with 50 professional customers ($199×50)
- Unlimited scaling with Enterprise plans

## 📝 License

MIT License - Build and sell your own SaaS!

---

**Built with ❤️ in Lahore, Pakistan | Selling Globally 🌍**

**Datum FleetX** - Precision in Motion 🚛
