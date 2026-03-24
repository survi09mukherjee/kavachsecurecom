require('dotenv').config();
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');
const rateLimit = require('express-rate-limit');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: { origin: '*' }
});

// Configure Global Rate Limiter
const apiLimiter = rateLimit({
	windowMs: 15 * 60 * 1000, // 15 minutes
	max: 100, // Limit each IP to 100 requests per `window`
	standardHeaders: true, 
	legacyHeaders: false, 
    message: { error: "Too many requests, please try again later." }
});

const authRoutes = require('./routes/authRoutes');

app.use(cors());
app.use(express.json());
app.use('/api/', apiLimiter); // Apply rate limiting to all API routes

// Main App API Routes
app.use('/api/v1/auth', authRoutes);

// Basic Health Check Route
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'healthy', service: 'Kavach SecureComm API' });
});

const socketManager = require('./websockets/socketManager');

// Setup WebSockets for Secure Messaging
socketManager(io);

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Backend Server & WebSockets running on port ${PORT}`);
});
