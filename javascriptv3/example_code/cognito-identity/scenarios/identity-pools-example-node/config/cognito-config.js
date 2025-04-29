// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

const { Issuer } = require('openid-client');

// Configure Cognito as an OIDC provider
const cognitoRegion = process.env.AWS_REGION;
const cognitoUserPoolId = process.env.COGNITO_USER_POOL_ID;
const cognitoClientId = process.env.COGNITO_APP_CLIENT_ID;
const cognitoClientSecret = process.env.COGNITO_APP_CLIENT_SECRET || undefined;
const appUrl = process.env.APP_URL || 'http://localhost:3000';

// Cognito OIDC discovery URL
const cognitoIssuerUrl = `https://cognito-idp.${cognitoRegion}.amazonaws.com/${cognitoUserPoolId}`;

// Initialize the OIDC client
let cognitoClient = null;

// Function to initialize the OIDC client
async function initializeOidcClient() {
  try {
    console.log(`Discovering OIDC issuer at: ${cognitoIssuerUrl}`);
    const cognitoIssuer = await Issuer.discover(cognitoIssuerUrl);
    
    console.log('Discovered issuer %s %O', cognitoIssuer.issuer, cognitoIssuer.metadata);
    
    // Create client
    cognitoClient = new cognitoIssuer.Client({
      client_id: cognitoClientId,
      client_secret: cognitoClientSecret,
      redirect_uris: [`${appUrl}/cognito/callback`],
      response_types: ['code'],
      token_endpoint_auth_method: cognitoClientSecret ? 'client_secret_basic' : 'none'
    });
    
    return cognitoClient;
  } catch (error) {
    console.error('Error initializing OIDC client:', error);
    throw error;
  }
}

module.exports = {
  cognitoIssuerUrl,
  cognitoClientId,
  cognitoUserPoolId,
  appUrl,
  
  // Get the OIDC client (initializing if needed)
  getClient: async () => {
    if (!cognitoClient) {
      await initializeOidcClient();
    }
    return cognitoClient;
  },
  
  // Generate the authorization URL
  getAuthorizationUrl: async (state, nonce) => {
    const client = await module.exports.getClient();
    return client.authorizationUrl({
      scope: 'openid email profile',
      state: state,
      nonce: nonce
    });
  },
  
  // Helper function to get AWS credentials from Cognito Identity Pool using OIDC tokens
  getAwsCredentialsWithOidcToken: async (idToken) => {
    const AWS = require('aws-sdk');
    
    // Configure the credentials provider to use your identity pool
    const identityPoolId = process.env.IDENTITY_POOL_ID;
    
    // Set up the Cognito credentials provider with the User Pool as the provider
    const logins = {};
    const providerName = `cognito-idp.${cognitoRegion}.amazonaws.com/${cognitoUserPoolId}`;
    logins[providerName] = idToken;
    
    console.log(`Getting AWS credentials with Cognito User Pool token`);
    console.log('Using identity pool ID:', identityPoolId);
    console.log('Using provider:', providerName);
    
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
  },
  
  // Get the Cognito domain URL
  getCognitoDomain: () => {
    return `https://${process.env.COGNITO_DOMAIN || `${cognitoUserPoolId}.auth.${cognitoRegion}.amazoncognito.com`}`;
  }
};
