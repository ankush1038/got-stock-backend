# 📈 GotStock - Stock Portfolio Backend API

A production-ready backend system for managing stock portfolios with authentication, alerts, and reporting features.

## 🚀 Features
- JWT Authentication (Login/Register)
- Add / Update / Delete Stocks
- Portfolio Tracking with Total Value
- Stock Alerts & Notifications (Email-based)
- PDF Report Generation
- Secure REST APIs (Spring Security)

## 🛠 Tech Stack
- Java 17
- Spring Boot
- Spring Security (JWT)
- PostgreSQL (Render)
- Docker
- Swagger (API Docs)

## 🌐 Live API
Base URL:
https://got-stock-backend.onrender.com

Swagger:
https://got-stock-backend.onrender.com/swagger-ui/index.html

## 🔐 Authentication
Use Bearer Token:
Authorization: Bearer <your_token>

## 📦 API Endpoints
| Method | Endpoint | Description |
|------|---------|------------|
| POST | /register | Register user |
| POST | /login | Login |
| POST | /add | Add stock |
| GET  | /get-stock | Get portfolio |
| PUT  | /update/{id} | Update stock |
| DELETE | /delete | Delete stock |
| POST | /alert | Set alert |
| POST | /report | Generate PDF |

## ⚙️ Environment Variables
- DB_URL
- EMAIL_USERNAME
- EMAIL_PASSWORD
- API_KEY

## 📌 Notes
- Free instance may take ~30-50 sec on first request
- Uses in-memory DB if env vars not set (dev mode)

## 👨‍💻 Author
Ankush Sharma
