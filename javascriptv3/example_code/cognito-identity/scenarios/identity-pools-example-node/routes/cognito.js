// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const cognitoConfig = require('../config/cognito-config');

// Generate a random string for state and nonce
function generateRandomString(length = 32) {
  return crypto.randomBytes(length).toString('hex');
}

// Login route - redirects to Cognito hosted UI
router.get('/login', async (req, res) => {
  try {
    // Generate state and nonce for OIDC flow
    const state = generateRandomString();
    const nonce = generateRandomString();
    
    // Store state and nonce in session for verification later
    req.session.cognitoAuthState = state;
    req.session.cognitoAuthNonce = nonce;
    
    // Generate authorization URL
    const authUrl = await cognitoConfig.getAuthorizationUrl(state, nonce);
    
    // Redirect to Cognito login page
    res.redirect(authUrl);
  } catch (error) {
    console.error('Error initiating login:', error);
    req.session.error = `Error initiating login: ${error.message}`;
    res.redirect('/error');
  }
});

// Callback route - handles the response from Cognito
router.get('/callback', async (req, res) => {
  try {
    const client = await cognitoConfig.getClient();
    
    // Verify state parameter to prevent CSRF attacks
    const state = req.session.cognitoAuthState;
    const nonce = req.session.cognitoAuthNonce;
    
    if (!state || req.query.state !== state) {
      throw new Error('State parameter mismatch');
    }
    
    // Exchange authorization code for tokens
    const params = client.callbackParams(req);
    const tokenSet = await client.callback(
      `${cognitoConfig.appUrl}/cognito/callback`,
      params,
      { state, nonce }
    );
    
    console.log('Received and validated tokens %j', tokenSet);
    
    // Get user info
    const userInfo = await client.userinfo(tokenSet);
    console.log('User info %j', userInfo);
    
    // Store tokens and user info in session
    req.session.cognitoTokens = {
      idToken: tokenSet.id_token,
      accessToken: tokenSet.access_token,
      refreshToken: tokenSet.refresh_token,
      expiresAt: tokenSet.expires_at
    };
    
    req.session.cognitoUser = {
      sub: userInfo.sub,
      username: userInfo.preferred_username || userInfo.email || userInfo.sub,
      email: userInfo.email,
      name: userInfo.name
    };
    
    // Get AWS credentials using the ID token
    try {
      const credentials = await cognitoConfig.getAwsCredentialsWithOidcToken(tokenSet.id_token);
      req.session.credentials = credentials;
      res.redirect('/credentials');
    } catch (credError) {
      console.error('Error getting AWS credentials:', credError);
      req.session.error = `Failed to get AWS credentials: ${credError.message}`;
      res.redirect('/error');
    }
  } catch (error) {
    console.error('Error processing callback:', error);
    req.session.error = `Error processing callback: ${error.message}`;
    res.redirect('/error');
  }
});

// Logout route
router.get('/logout', async (req, res) => {
  try {
    // Clear session
    req.session.destroy((err) => {
      if (err) {
        console.error('Error destroying session:', err);
      }
      
      // Construct the logout URL with the correct parameter name
      // For Cognito User Pools, we need to use the hosted UI domain
      const cognitoDomain = `https://${process.env.COGNITO_DOMAIN || `${process.env.COGNITO_USER_POOL_ID}.auth.${process.env.AWS_REGION}.amazoncognito.com`}`;
      const logoutUrl = `${cognitoDomain}/logout?client_id=${process.env.COGNITO_APP_CLIENT_ID}&logout_uri=${encodeURIComponent(cognitoConfig.appUrl)}`;
      
      console.log('Redirecting to logout URL:', logoutUrl);
      res.redirect(logoutUrl);
    });
  } catch (error) {
    console.error('Error during logout:', error);
    res.redirect('/');
  }
});

// Refresh tokens route
router.get('/refresh', async (req, res) => {
  try {
    // Check if we have a refresh token
    if (!req.session.cognitoTokens?.refreshToken) {
      return res.status(401).json({ error: 'No refresh token available' });
    }
    
    const client = await cognitoConfig.getClient();
    
    // Use refresh token to get new tokens
    const tokenSet = await client.refresh(req.session.cognitoTokens.refreshToken);
    
    console.log('Refreshed tokens %j', tokenSet);
    
    // Update tokens in session
    req.session.cognitoTokens = {
      idToken: tokenSet.id_token,
      accessToken: tokenSet.access_token,
      refreshToken: tokenSet.refresh_token || req.session.cognitoTokens.refreshToken,
      expiresAt: tokenSet.expires_at
    };
    
    // Get new AWS credentials
    const credentials = await cognitoConfig.getAwsCredentialsWithOidcToken(tokenSet.id_token);
    req.session.credentials = credentials;
    
    res.json({ success: true, message: 'Tokens refreshed successfully' });
  } catch (error) {
    console.error('Error refreshing tokens:', error);
    res.status(500).json({ error: `Error refreshing tokens: ${error.message}` });
  }
});

module.exports = router;
