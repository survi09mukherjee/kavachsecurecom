/**
 * Middleware to enforce Role-Based Access Control (RBAC) on REST routes.
 * Assumes that `req.user` contains the authenticated user's role (extracted from JWT).
 */

const ROLE_PERMISSIONS = {
    'Officer': ['send_emergency', 'manage_groups', 'view_all_directory'],
    'Soldier': ['participate_secure_messaging', 'view_unit_directory'],
    'Family': ['communicate_with_soldier']
};

const requireRole = (allowedRoles) => {
    return (req, res, next) => {
        const userRole = req.user?.role; // Should be set by JWT auth middleware
        
        if (!userRole) {
            return res.status(401).json({ error: 'Unauthorized: No role specified' });
        }

        if (!allowedRoles.includes(userRole)) {
            return res.status(403).json({ error: 'Forbidden: Insufficient privileges' });
        }
        
        next();
    };
};

const requirePermission = (permission) => {
    return (req, res, next) => {
        const userRole = req.user?.role;
        
        if (!userRole || !ROLE_PERMISSIONS[userRole]) {
            return res.status(401).json({ error: 'Unauthorized: Invalid role' });
        }

        if (!ROLE_PERMISSIONS[userRole].includes(permission)) {
            return res.status(403).json({ error: 'Forbidden: Missing necessary permission' });
        }
        
        next();
    };
};

module.exports = {
    requireRole,
    requirePermission
};
