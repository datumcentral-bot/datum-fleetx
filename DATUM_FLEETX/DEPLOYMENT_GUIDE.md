# 🚀 Datum FleetX Deployment Guide

## Free Tier Deployment (No Credit Card Required)

---

## Step 1: Create Neon PostgreSQL Database (Free)

1. Go to **https://neon.tech**
2. Sign up with GitHub account
3. Click "Create Project"
4. Name: `datum-fleetx`
5. Copy the connection string (it looks like):
   ```
   postgresql://username:password@ep-xyz.us-east-1.aws.neon.tech/fleetx?sslmode=require
   ```

---

## Step 2: Deploy Backend on Render (Free)

1. Go to **https://render.com**
2. Sign up with GitHub account
3. Click "New" → "Web Service"
4. Connect your GitHub repository
5. Settings:
   - Name: `datum-fleetx-backend`
   - Runtime: `Docker`
   - Docker Command: `java -jar app.jar`
6. Add Environment Variables:
   - `DB_URL`: Your Neon connection string
   - `DB_USERNAME`: From your Neon URL
   - `DB_PASSWORD`: Your Neon password
   - `JWT_SECRET`: Any secure random string
7. Click "Deploy"

---

## Step 3: Deploy Frontend on Vercel (Free)

1. Go to **https://vercel.com**
2. Sign up with GitHub account
3. Click "Add New..." → "Project"
4. Import your GitHub repository
5. Settings:
   - Framework Preset: `Vite`
   - Build Command: `npm run build`
   - Output Directory: `dist`
6. Add Environment Variable:
   - `VITE_API_URL`: Your Render backend URL (e.g., `https://datum-fleetx-backend.onrender.com/api/v1`)
7. Click "Deploy"

---

## Step 4: Update CORS

After deploying backend, add your Vercel URL to CORS:
1. In Render dashboard, update `CORS_ORIGINS` env var
2. Value: `https://your-app.vercel.app`

---

## Alternative: Railway (All-in-One)

1. Go to **https://railway.app**
2. Sign up with GitHub
3. Click "New Project" → "Deploy PostgreSQL"
4. Copy DATABASE_URL
5. Click "New Project" → "Deploy GitHub Repo"
6. Connect backend repo
7. Add DATABASE_URL to env vars

---

## Quick Test Commands

```bash
# Test backend
curl https://your-backend.onrender.com/api/v1/health

# Test frontend
curl https://your-frontend.vercel.app
```

---

## Cost: $0/month (Forever on Free Tier)

- Neon: Free tier includes 0.5GB storage
- Render: Free tier includes 750 hours
- Vercel: Free tier includes 100GB bandwidth

---

## Support

If you need help, check these resources:
- Neon Docs: https://neon.tech/docs
- Render Docs: https://render.com/docs
- Vercel Docs: https://vercel.com/docs
