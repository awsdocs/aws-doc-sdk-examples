// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const passport = require('passport');
const FacebookStrategy = require('passport-facebook').Strategy;
const TwitterStrategy = require('passport-twitter').Strategy;
const AmazonStrategy = require('passport-amazon').Strategy;
const AppleStrategy = require('passport-apple');
const GoogleStrategy = require('passport-google-oauth20').Strategy;
const axios = require('axios');

// Serialize user
passport.serializeUser((user, done) => {
  done(null, user);
});

// Deserialize user
passport.deserializeUser((user, done) => {
  done(null, user);
});

// Facebook Strategy
if (process.env.FACEBOOK_APP_ID && process.env.FACEBOOK_APP_SECRET) {
  passport.use(new FacebookStrategy({
    clientID: process.env.FACEBOOK_APP_ID,
    clientSecret: process.env.FACEBOOK_APP_SECRET,
    callbackURL: `${process.env.APP_URL}/auth/facebook/callback`,
    profileFields: ['id', 'displayName', 'email']
  }, (accessToken, refreshToken, profile, done) => {
    // For Facebook, we need to get the ID token for Cognito
    return done(null, {
      provider: 'facebook',
      id: profile.id,
      displayName: profile.displayName,
      accessToken: accessToken,
      needsTokenExchange: true
    });
  }));
}

// Twitter Strategy
if (process.env.TWITTER_CONSUMER_KEY && process.env.TWITTER_CONSUMER_SECRET) {
  console.log('Configuring Twitter strategy with:');
  console.log('- Consumer Key:', process.env.TWITTER_CONSUMER_KEY.substring(0, 5) + '...');
  console.log('- Callback URL:', `${process.env.APP_URL}/auth/twitter/callback`);
  
  // Check if we have the newer Twitter API v2 credentials
  if (process.env.TWITTER_CLIENT_ID && process.env.TWITTER_CLIENT_SECRET) {
    console.log('- Twitter API v2 credentials detected');
    console.log('- Client ID:', process.env.TWITTER_CLIENT_ID.substring(0, 5) + '...');
  }
  
  passport.use(new TwitterStrategy({
    consumerKey: process.env.TWITTER_CONSUMER_KEY,
    consumerSecret: process.env.TWITTER_CONSUMER_SECRET,
    callbackURL: `${process.env.APP_URL}/auth/twitter/callback`,
    includeEmail: true
  }, (token, tokenSecret, profile, done) => {
    console.log('Twitter authentication callback received');
    console.log('Profile:', profile.id, profile.displayName);
    console.log('Token details:', {
      token: token ? token.substring(0, 10) + '...' : 'missing',
      tokenSecret: tokenSecret ? tokenSecret.substring(0, 5) + '...' : 'missing'
    });
    
    // For Twitter, we need both token and tokenSecret for Cognito
    // The format must be exactly "token;secret" with a semicolon separator
    const combinedToken = `${token};${tokenSecret}`;
    
    console.log('Created combined token:', combinedToken.substring(0, 20) + '...');
    
    const user = {
      provider: 'twitter',
      id: profile.id,
      displayName: profile.displayName,
      token: combinedToken
    };
    
    return done(null, user);
  }));
}

// Amazon Strategy
if (process.env.AMAZON_CLIENT_ID && process.env.AMAZON_CLIENT_SECRET) {
  passport.use(new AmazonStrategy({
    clientID: process.env.AMAZON_CLIENT_ID,
    clientSecret: process.env.AMAZON_CLIENT_SECRET,
    callbackURL: `${process.env.APP_URL}/auth/amazon/callback`,
    scope: ['profile']
  }, (accessToken, refreshToken, profile, done) => {
    console.log('Amazon profile:', profile);
    console.log('Amazon access token:', accessToken.substring(0, 20) + '...');
    
    // For Amazon, we use the access token directly with Cognito
    return done(null, {
      provider: 'amazon',
      id: profile.id,
      displayName: profile.displayName,
      accessToken: accessToken,
      profile: profile
    });
  }));
}

// Apple Strategy
if (process.env.APPLE_CLIENT_ID && process.env.APPLE_TEAM_ID && process.env.APPLE_KEY_ID) {
  passport.use(new AppleStrategy({
    clientID: process.env.APPLE_CLIENT_ID,
    teamID: process.env.APPLE_TEAM_ID,
    keyID: process.env.APPLE_KEY_ID,
    keyFilePath: process.env.APPLE_PRIVATE_KEY_PATH,
    callbackURL: `${process.env.APP_URL}/auth/apple/callback`,
    passReqToCallback: true
  }, (req, accessToken, refreshToken, idToken, profile, done) => {
    // Apple provides the idToken directly, which is what we need for Cognito
    return done(null, {
      provider: 'apple',
      id: profile.id || 'apple-user',
      displayName: profile.name?.firstName || 'Apple User',
      token: idToken // Use idToken for Cognito
    });
  }));
}

// Google Strategy
if (process.env.GOOGLE_CLIENT_ID && process.env.GOOGLE_CLIENT_SECRET) {
  passport.use(new GoogleStrategy({
    clientID: process.env.GOOGLE_CLIENT_ID,
    clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    callbackURL: `${process.env.APP_URL}/auth/google/callback`,
    scope: ['profile', 'email', 'openid'],
    // Request both access_token and id_token
    accessType: 'offline',
    // This is important - it makes Google return the id_token
    responseType: 'code id_token',
    // Include the id_token in the profile
    includeGrantedScopes: true
  }, (accessToken, refreshToken, params, profile, done) => {
    // The id_token should be available in the params object
    // If params contains id_token, use it directly
    const idToken = params && params.id_token;
    
    return done(null, {
      provider: 'google',
      id: profile.id,
      displayName: profile.displayName,
      accessToken: accessToken,
      idToken: idToken, // Store the ID token if available
      refreshToken: refreshToken,
      needsTokenExchange: !idToken // Only need exchange if we don't have the ID token
    });
  }));
}
