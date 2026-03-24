const crypto = require('crypto');
const db = require('../db');

/**
 * Generate a new invite token (Admin/Officer only)
 */
const generateInviteToken = async (req, res) => {
  try {
    const { role, issuerId } = req.body; // In real app, issuerId comes from JWT
    
    // Validate role
    if (!['Officer', 'Soldier', 'Family'].includes(role)) {
      return res.status(400).json({ error: 'Invalid role specified' });
    }

    // Generate secure random token
    const token = crypto.randomBytes(32).toString('hex');
    const tokenHash = crypto.createHash('sha256').update(token).digest('hex');

    // Token expires in 24 hours
    const expiresAt = new Date();
    expiresAt.setHours(expiresAt.getHours() + 24);

    await db.query(
      `INSERT INTO invite_tokens (token_hash, role, issuer_id, expires_at) 
       VALUES ($1, $2, $3, $4)`,
      [tokenHash, role, issuerId, expiresAt]
    );

    // Return the raw token ONLY ONCE. The DB only stores the hash.
    res.status(201).json({ 
        message: 'Invite token generated successfully', 
        token: token,
        role: role,
        expiresAt: expiresAt
    });

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * Validate an invite token during user onboarding
 */
const validateInviteToken = async (req, res) => {
  try {
    const { token } = req.body;
    if (!token) return res.status(400).json({ error: 'Token is required' });

    const tokenHash = crypto.createHash('sha256').update(token).digest('hex');

    const result = await db.query(
      `SELECT * FROM invite_tokens 
       WHERE token_hash = $1 AND is_used = FALSE AND expires_at > NOW()`,
      [tokenHash]
    );

    if (result.rows.length === 0) {
      return res.status(400).json({ error: 'Invalid or expired invite token' });
    }

    const invite = result.rows[0];
    res.status(200).json({ 
        message: 'Token is valid', 
        role: invite.role 
    });

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * Register a new device and user identity using a valid token
 */
const registerDevice = async (req, res) => {
    try {
        const { token, publicIdentityKey, deviceId, keycloakId } = req.body;

        if (!token || !publicIdentityKey || !deviceId || !keycloakId) {
            return res.status(400).json({ error: 'Missing required fields' });
        }

        const tokenHash = crypto.createHash('sha256').update(token).digest('hex');

        // Start a transaction
        await db.query('BEGIN');

        // Check if token is valid and lock the row
        const inviteResult = await db.query(
            `SELECT * FROM invite_tokens 
             WHERE token_hash = $1 AND is_used = FALSE AND expires_at > NOW() FOR UPDATE`,
            [tokenHash]
        );

        if (inviteResult.rows.length === 0) {
            await db.query('ROLLBACK');
            return res.status(400).json({ error: 'Invalid or expired invite token' });
        }

        const role = inviteResult.rows[0].role;

        // Create the user
        const userResult = await db.query(
            `INSERT INTO users (keycloak_id, role, public_identity_key, device_id)
             VALUES ($1, $2, $3, $4) RETURNING id, role`,
            [keycloakId, role, publicIdentityKey, deviceId]
        );

        // Mark token as used
        await db.query(
            `UPDATE invite_tokens SET is_used = TRUE WHERE token_hash = $1`,
            [tokenHash]
        );

        await db.query('COMMIT');

        res.status(201).json({
            message: 'Device registered securely',
            userId: userResult.rows[0].id,
            role: userResult.rows[0].role
        });

    } catch (err) {
        await db.query('ROLLBACK');
        console.error(err);
        
        // Handle unique constraint violations
        if (err.code === '23505') {
            return res.status(400).json({ error: 'Device or Identity already registered' });
        }
        res.status(500).json({ error: 'Internal server error' });
    }
}

module.exports = {
  generateInviteToken,
  validateInviteToken,
  registerDevice
};
