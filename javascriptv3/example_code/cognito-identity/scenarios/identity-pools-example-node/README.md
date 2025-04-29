# Amazon Cognito Identity Pools Demo

This is a NodeJS web application that demonstrates how to use Amazon Cognito Identity Pools to exchange social provider tokens for temporary AWS credentials.

## Features

- Authentication with multiple social identity providers:
  - Facebook
  - Twitter
  - Login with Amazon
  - Sign in with Apple
  - Google
- Complete OAuth flow handling
- Token exchange with Amazon Cognito Identity Pools
- Display of temporary AWS credentials

## Prerequisites

- Node.js (v12 or later)
- An AWS account
- A Cognito Identity Pool configured with the desired social identity providers
- Developer accounts with the social providers you want to use

## Setup

1. Clone this repository
2. Install dependencies:
   ```
   npm install
   ```
3. Create a `.env` file based on the `.env.example` template:
   ```
   cp .env.example .env
   ```
4. Update the `.env` file with your AWS region, Identity Pool ID, and social provider credentials

## Configuration

### Creating an Identity Pool

1. Go to the AWS Management Console and navigate to Amazon Cognito
2. Choose "Identity Pools" and click "Create new identity pool"
3. Enter a name for your identity pool
4. Configure authentication providers:
   - Under the "Authentication providers" section, select the social providers you want to use
   - Enter the App IDs for each provider
5. Configure IAM roles:
   - Create new IAM roles or use existing ones
   - Make sure the roles have appropriate permissions for your use case
6. Complete the creation process and note your Identity Pool ID

### Configuring Social Providers

Each social provider requires specific setup:

#### Facebook
1. Go to the [Facebook Developers](https://developers.facebook.com/) portal
2. Create a new app
3. Add the Facebook Login product
4. Configure the OAuth redirect URI: `http://localhost:3000/auth/facebook/callback`
5. Get your App ID and App Secret

#### Twitter
1. Go to the [Twitter Developer Portal](https://developer.twitter.com/)
2. Create a new app
3. Configure the callback URL: `http://localhost:3000/auth/twitter/callback`
4. Get your API Key (Consumer Key) and API Secret (Consumer Secret)

#### Amazon
1. Go to the [Amazon Developer Portal](https://developer.amazon.com/)
2. Register a new security profile
3. Configure the allowed return URLs: `http://localhost:3000/auth/amazon/callback`
4. Get your Client ID and Client Secret

#### Apple
1. Go to the [Apple Developer Portal](https://developer.apple.com/)
2. Register an App ID
3. Enable "Sign In with Apple"
4. Create a Services ID
5. Configure the return URL: `http://localhost:3000/auth/apple/callback`
6. Create a key and download the private key file
7. Note your Team ID, Key ID, and Client ID

#### Google
1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Configure the OAuth consent screen
4. Create OAuth client ID credentials
5. Add the authorized redirect URI: `http://localhost:3000/auth/google/callback`
6. Get your Client ID and Client Secret

## Running the Application

1. Start the server:
   ```
   npm start
   ```
2. Open your browser and navigate to `http://localhost:3000`
3. Click on a social provider button to initiate the authentication flow
4. After successful authentication, you'll see your temporary AWS credentials

## How It Works

1. The user clicks on a social provider button
2. The application redirects to the provider's authentication page
3. The user authenticates with the provider
4. The provider redirects back to the application with an authentication token
5. The application uses the AWS SDK to exchange the token for temporary AWS credentials
6. The credentials are displayed to the user

## Security Considerations

- This demo is for educational purposes only
- In a production environment, implement proper security measures:
  - Use HTTPS
  - Implement CSRF protection
  - Add rate limiting
  - Store sensitive information securely
  - Implement proper error handling and logging

## Additional Resources

- [Amazon Cognito Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/what-is-amazon-cognito.html)
- [AWS SDK for JavaScript Documentation](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/welcome.html)
- [Passport.js Documentation](http://www.passportjs.org/)
