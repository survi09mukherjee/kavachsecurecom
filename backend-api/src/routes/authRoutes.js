const express = require('express');
const { generateInviteToken, validateInviteToken, registerDevice } = require('../controllers/authController');

const router = express.Router();

// Route to generate a new invite token (Requires Admin/Officer Privileges in Prod)
router.post('/invite', generateInviteToken);

// Route to validate a token before full registration
router.post('/validate', validateInviteToken);

// Route to finalise device registration and upload public identity key
router.post('/register', registerDevice);

module.exports = router;
