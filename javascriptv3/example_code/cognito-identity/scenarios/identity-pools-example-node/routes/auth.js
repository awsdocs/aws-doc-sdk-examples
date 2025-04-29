// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const express = require('express');
const passport = require('passport');
const AWS = require('aws-sdk');
const axios = require('axios');
const router = express.Router();

// Helper function to get Google ID token
async function getGoogleIdToken(accessToken, existingIdToken) {
  try {
    // If we already have an ID token from the OAuth flow, use it
    if (existingIdToken) {
      console.log('Using ID token provided by Google OAuth flow');
      return existingIdToken;
    }
    
    // Otherwise, we need to exchange the access token for token info
    // Note: This is a fallback method and may not always work as expected
    console.log('No ID token available, attempting to get token info');
    
    // For Google, we can use the tokeninfo endpoint to verify the token
    const response = await axios.get('https://oauth2.googleapis.com/tokeninfo', {
      params: { access_token: accessToken }
    });
    
    console.log('Google token info response:', response.data);
    
    // Unfortunately, this endpoint doesn't return an actual ID token
    // We would need to use the OAuth2 server flow to get a proper ID token
    // This is just for debugging purposes
    
    // In a production environment, you should implement a proper token exchange
    // using the refresh token and Google's OAuth2 token endpoint
    
    throw new Error('No valid ID token available from Google. Please check your OAuth configuration.');
  } catch (error) {
    console.error('Error with Google token:', error);
    throw error;
  }
}

// Helper function to exchange Facebook access token for a longer-lived token
async function getFacebookToken(accessToken) {
  try {
    const response = await axios.get('https://graph.facebook.com/oauth/access_token', {
      params: {
        grant_type: 'fb_exchange_token',
        client_id: process.env.FACEBOOK_APP_ID,
        client_secret: process.env.FACEBOOK_APP_SECRET,
        fb_exchange_token: accessToken
      }
    });
    
    console.log('Facebook token exchange response:', response.data);
    
    if (response.data && response.data.access_token) {
      return response.data.access_token;
    }
    return accessToken; // Fall back to original token if exchange fails
  } catch (error) {
    console.error('Error exchanging Facebook token:', error);
    return accessToken; // Fall back to original token
  }
}

// Helper function to get AWS credentials from Cognito
async function getAwsCredentials(provider, token) {
  // Configure the credentials provider to use your identity pool
  const identityPoolId = process.env.IDENTITY_POOL_ID;
  
  // Set up the Cognito credentials provider based on the social provider
  const logins = {};
  
  switch (provider) {
    case 'amazon':
      // For Amazon Login with Amazon, the provider name must be exactly as specified in the identity pool
      logins['www.amazon.com'] = token;
      break;
    case 'google':
      logins['accounts.google.com'] = token;
      break;
    case 'apple':
      logins['appleid.apple.com'] = token;
      break;
    case 'facebook':
      logins['graph.facebook.com'] = token;
      break;
    case 'twitter':
      // Twitter requires a specific format: "token;secret"
      // Log the token format to help with debugging
      console.log('Twitter token format check:', {
        tokenType: typeof token,
        tokenLength: token.length,
        containsSemicolon: token.includes(';'),
        firstPart: token.split(';')[0]?.substring(0, 10) + '...',
        secondPart: token.split(';')[1] ? (token.split(';')[1].substring(0, 5) + '...') : 'missing'
      });
      logins['api.twitter.com'] = token;
      break;
    default:
      throw new Error('Invalid provider');
  }
  
  console.log(`Getting AWS credentials for provider ${provider} with token:`, token.substring(0, 20) + '...');
  console.log('Using identity pool ID:', identityPoolId);
  console.log('Login provider key:', Object.keys(logins)[0]);
  
  // For Twitter, log additional details about the token
  if (provider === 'twitter') {
    console.log('Twitter token details:', {
      format: token.includes(';') ? 'Contains semicolon separator' : 'Missing semicolon separator',
      parts: token.split(';').length,
      firstPartLength: token.split(';')[0]?.length || 0,
      secondPartLength: token.split(';')[1]?.length || 0
    });
  }
  
  // Create the credentials object
  const credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: identityPoolId,
    Logins: logins
  });
  
  // Get the credentials
  await credentials.getPromise();
  
  // Return the temporary AWS credentials
  return {
    accessKeyId: credentials.accessKeyId,
    secretAccessKey: credentials.secretAccessKey,
    sessionToken: credentials.sessionToken,
    expiration: credentials.expireTime,
    identityId: credentials.identityId
  };
}

// Facebook routes
router.get('/facebook', passport.authenticate('facebook'));
router.get('/facebook/callback', 
  passport.authenticate('facebook', { failureRedirect: '/' }),
  async (req, res) => {
    try {
      if (req.user && req.user.accessToken) {
        // Exchange the access token for a longer-lived token if needed
        const token = await getFacebookToken(req.user.accessToken);
        const credentials = await getAwsCredentials('facebook', token);
        req.session.credentials = credentials;
        res.redirect('/credentials');
      } else {
        throw new Error('Authentication failed');
      }
    } catch (error) {
      console.error('Error getting AWS credentials:', error);
      req.session.error = error.message;
      req.session.debug = {
        provider: 'facebook',
        user: req.user,
        error: error.toString()
      };
      res.redirect('/error');
    }
  }
);

// Twitter routes
router.get('/twitter', (req, res, next) => {
  console.log('Starting Twitter authentication');
  console.log('Twitter credentials:', {
    consumerKey: process.env.TWITTER_CONSUMER_KEY ? 'Set (first few chars: ' + process.env.TWITTER_CONSUMER_KEY.substring(0, 5) + '...)' : 'Not set',
    consumerSecret: process.env.TWITTER_CONSUMER_SECRET ? 'Set (length: ' + process.env.TWITTER_CONSUMER_SECRET.length + ')' : 'Not set',
    clientId: process.env.TWITTER_CLIENT_ID ? 'Set' : 'Not set',
    clientSecret: process.env.TWITTER_CLIENT_SECRET ? 'Set' : 'Not set',
    callbackUrl: `${process.env.APP_URL}/auth/twitter/callback`
  });
  
  // Check if the credentials in the .env file match what's in the identity pool
  console.log('Checking if Twitter credentials match identity pool configuration...');
  console.log('Note: If authentication fails, you may need to update the identity pool configuration');
  
  passport.authenticate('twitter', (err) => {
    if (err) {
      console.error('Error initiating Twitter authentication:', err);
      req.session.error = `Error initiating Twitter authentication: ${err.message}`;
      req.session.debug = {
        provider: 'twitter',
        error: err.toString()
      };
      return res.redirect('/error');
    }
    // This callback won't be called on success, as the user will be redirected to Twitter
  })(req, res, next);
});

router.get('/twitter/callback', (req, res, next) => {
  console.log('Received callback from Twitter');
  console.log('Query parameters:', req.query);
  
  passport.authenticate('twitter', (err, user, info) => {
    if (err) {
      console.error('Twitter authentication error:', err);
      req.session.error = `Twitter authentication error: ${err.message}`;
      req.session.debug = {
        provider: 'twitter',
        error: err.toString(),
        info: info,
        query: req.query
      };
      return res.redirect('/error');
    }
    
    if (!user) {
      console.error('Twitter authentication failed, no user returned');
      req.session.error = 'Twitter authentication failed';
      req.session.debug = {
        provider: 'twitter',
        info: info,
        query: req.query
      };
      return res.redirect('/error');
    }
    
    console.log('Twitter authentication successful, user:', {
      id: user.id,
      displayName: user.displayName,
      tokenFormat: user.token ? `${user.token.substring(0, 10)}...;${user.token.split(';')[1]?.substring(0, 5)}...` : 'missing'
    });
    
    // Log in the user
    req.logIn(user, async (loginErr) => {
      if (loginErr) {
        console.error('Error logging in Twitter user:', loginErr);
        req.session.error = `Error logging in: ${loginErr.message}`;
        req.session.debug = {
          provider: 'twitter',
          error: loginErr.toString()
        };
        return res.redirect('/error');
      }
      
      try {
        if (!user.token) {
          throw new Error('No token received from Twitter');
        }
        
        // Check if the token has the correct format
        if (!user.token.includes(';')) {
          throw new Error('Twitter token is not in the correct format (missing semicolon separator)');
        }
        
        const [oauthToken, oauthTokenSecret] = user.token.split(';');
        if (!oauthToken || !oauthTokenSecret) {
          throw new Error('Twitter token is missing either the token or secret part');
        }
        
        console.log('Getting AWS credentials with Twitter token');
        const credentials = await getAwsCredentials('twitter', user.token);
        req.session.credentials = credentials;
        return res.redirect('/credentials');
      } catch (error) {
        console.error('Error getting AWS credentials for Twitter:', error);
        req.session.error = `Failed to get AWS credentials: ${error.message}`;
        req.session.debug = {
          provider: 'twitter',
          userId: user.id,
          displayName: user.displayName,
          tokenFormat: user.token ? (user.token.includes(';') ? 'Has semicolon' : 'No semicolon') : 'No token',
          error: error.toString()
        };
        return res.redirect('/error');
      }
    });
  })(req, res, next);
});

// Amazon routes
router.get('/amazon', passport.authenticate('amazon', { scope: ['profile'] }));
router.get('/amazon/callback', 
  passport.authenticate('amazon', { failureRedirect: '/' }),
  async (req, res) => {
    try {
      if (req.user && req.user.accessToken) {
        console.log('Amazon authentication successful');
        console.log('Amazon user profile:', req.user);
        
        // For Amazon, we use the access token directly
        try {
          const credentials = await getAwsCredentials('amazon', req.user.accessToken);
          req.session.credentials = credentials;
          res.redirect('/credentials');
        } catch (credError) {
          console.error('Error getting AWS credentials for Amazon:', credError);
          req.session.error = `Failed to get AWS credentials: ${credError.message}`;
          req.session.debug = {
            provider: 'amazon',
            user: req.user,
            error: credError.toString(),
            identityPoolId: process.env.IDENTITY_POOL_ID,
            amazonClientId: process.env.AMAZON_CLIENT_ID
          };
          res.redirect('/error');
        }
      } else {
        throw new Error('Authentication failed');
      }
    } catch (error) {
      console.error('Error in Amazon authentication flow:', error);
      req.session.error = error.message;
      req.session.debug = {
        provider: 'amazon',
        user: req.user,
        error: error.toString()
      };
      res.redirect('/error');
    }
  }
);

// Apple routes
router.get('/apple', passport.authenticate('apple'));
router.get('/apple/callback', 
  passport.authenticate('apple', { failureRedirect: '/' }),
  async (req, res) => {
    try {
      if (req.user && req.user.token) {
        const credentials = await getAwsCredentials('apple', req.user.token);
        req.session.credentials = credentials;
        res.redirect('/credentials');
      } else {
        throw new Error('Authentication failed');
      }
    } catch (error) {
      console.error('Error getting AWS credentials:', error);
      req.session.error = error.message;
      req.session.debug = {
        provider: 'apple',
        user: req.user,
        error: error.toString()
      };
      res.redirect('/error');
    }
  }
);

// Google routes
router.get('/google', passport.authenticate('google', { 
  scope: ['profile', 'email', 'openid'],
  accessType: 'offline',
  prompt: 'consent'
}));

router.get('/google/callback', 
  passport.authenticate('google', { failureRedirect: '/' }),
  async (req, res) => {
    try {
      if (!req.user) {
        throw new Error('Authentication failed');
      }
      
      // Check if we have an ID token from the OAuth flow
      if (req.user.idToken) {
        console.log('Using ID token from Google OAuth flow');
        
        // Use the ID token with Cognito
        const credentials = await getAwsCredentials('google', req.user.idToken);
        req.session.credentials = credentials;
        res.redirect('/credentials');
      } 
      // Fall back to access token if no ID token (should be fixed by our changes)
      else if (req.user.accessToken) {
        console.log('No ID token available, attempting to get one');
        
        try {
          // Try to get an ID token
          const idToken = await getGoogleIdToken(req.user.accessToken, null);
          
          // Use the ID token with Cognito
          const credentials = await getAwsCredentials('google', idToken);
          req.session.credentials = credentials;
          res.redirect('/credentials');
        } catch (tokenError) {
          req.session.error = `Failed to get valid Google ID token: ${tokenError.message}`;
          req.session.debug = {
            provider: 'google',
            user: req.user,
            error: tokenError.toString()
          };
          res.redirect('/error');
        }
      } else {
        throw new Error('No token available from Google');
      }
    } catch (error) {
      console.error('Error in Google authentication flow:', error);
      req.session.error = error.message;
      req.session.debug = {
        provider: 'google',
        user: req.user,
        error: error.toString()
      };
      res.redirect('/error');
    }
  }
);

// Logout route
router.get('/logout', (req, res) => {
  req.logout(function(err) {
    if (err) { return next(err); }
    req.session.destroy();
    res.redirect('/');
  });
});

module.exports = router;
