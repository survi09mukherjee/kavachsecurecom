const jwt = require('jsonwebtoken');

/**
 * Middleware to validate JWT tokens for API Authentication
 * Ensures Zero-Trust Server Logic by denying all unauthenticated requests.
 */
const requireAuth = (req, res, next) => {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Unauthorized: Missing or invalid token' });
    }

    const token = authHeader.split(' ')[1];

    try {
        // In reality, verify with Keycloak public certs. We use a secret here.
        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'fallback_secret');
        req.user = decoded; // Contains id, role, device_id
        next();
    } catch (err) {
        return res.status(403).json({ error: 'Forbidden: Token expired or invalid' });
    }
};

module.exports = { requireAuth };
