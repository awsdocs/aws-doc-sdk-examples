// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const express = require('express');
const session = require('express-session');
const passport = require('passport');
const AWS = require('aws-sdk');
const dotenv = require('dotenv');
const path = require('path');

// Load environment variables
dotenv.config();

// Import authentication strategies
require('./config/passport-config');

const app = express();
const port = process.env.PORT || 3000;

// Configure middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

// Configure session
app.use(session({
  secret: process.env.SESSION_SECRET || 'your-secret-key',
  resave: false,
  saveUninitialized: true,
  cookie: { secure: process.env.NODE_ENV === 'production' }
}));

// Initialize Passport
app.use(passport.initialize());
app.use(passport.session());

// Set up view engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Configure AWS SDK
AWS.config.region = process.env.AWS_REGION || 'us-east-1';

// Import routes
const authRoutes = require('./routes/auth');
const cognitoRoutes = require('./routes/cognito');
const indexRoutes = require('./routes/index');

// Use routes
app.use('/auth', authRoutes);
app.use('/cognito', cognitoRoutes);
app.use('/', indexRoutes);

// Error handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).render('error', { 
    message: 'Something went wrong!',
    error: process.env.NODE_ENV === 'development' ? err : {}
  });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
