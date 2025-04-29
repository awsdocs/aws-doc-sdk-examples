// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const express = require('express');
const router = express.Router();

// Home page
router.get('/', (req, res) => {
  res.render('index', { 
    user: req.user,
    cognitoUser: req.session.cognitoUser,
    title: 'Cognito Identity Pools Demo'
  });
});

// Login page
router.get('/login', (req, res) => {
  res.render('login', { 
    error: req.query.error,
    title: 'Login'
  });
});

// Credentials page - protected route
router.get('/credentials', (req, res) => {
  if (!req.session.credentials) {
    return res.redirect('/');
  }
  
  res.render('credentials', {
    user: req.user,
    cognitoUser: req.session.cognitoUser,
    cognitoTokens: req.session.cognitoTokens,
    credentials: req.session.credentials,
    title: 'AWS Credentials'
  });
});

// Error page
router.get('/error', (req, res) => {
  const errorMessage = req.session.error || 'An unknown error occurred';
  const debugInfo = req.session.debug || null;
  
  // Clear session error data after displaying it
  delete req.session.error;
  delete req.session.debug;
  
  res.render('error', {
    message: errorMessage,
    debug: debugInfo
  });
});

module.exports = router;
