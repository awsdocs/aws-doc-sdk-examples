# Amazon Cognito identity pools authentication flows demo

This interactive web application demonstrates how to use Amazon Cognito identity pools with multiple identity providers. The demo shows both enhanced flow and basic flow authentication patterns, providing developers with hands-on examples of real-world integration.

## About this demo

Amazon Cognito identity pools enable you to create unique identities for your users and federate them with identity providers. This demo focuses on:

* Demonstrating both enhanced and basic authentication flows in identity pools with detailed API call breakdowns
* Implementing guest (unauthenticated) access to provide limited AWS service access without requiring sign-in
* Integrating supported identity providers for different use cases:
  * Social identity providers (Facebook, Amazon, and Google) for consumer access
  * Enterprise identity providers (through OIDC or SAML) for corporate users
  * Amazon Cognito user pools for direct user management
* Showing how to exchange identity provider tokens for temporary AWS credentials
* Demonstrating how to use these credentials to access AWS services securely

**Note:** This demo is designed for educational purposes to help you understand identity pools and choose the right authentication approach for your applications.

## What's included

This demo includes the following features:

### Authentication flows
* **Enhanced flow** – The recommended authentication flow that combines `GetId` and `GetCredentialsForIdentity` operations into a streamlined process
* **Basic flow** – The traditional authentication flow with separate `GetId`, `GetOpenIdToken`, and `AssumeRoleWithWebIdentity` operations
* **Interactive comparison** – Side-by-side demonstration of both flows with detailed API call visualization

### Supported identity providers

#### Social identity providers
* **Google** – OAuth 2.0 implementation with Google Sign-In
* **Facebook** – Facebook Login integration
* **Login with Amazon** – Amazon OAuth 2.0 implementation

#### Enterprise identity providers
* **OpenID Connect (OIDC)** – Generic OIDC provider support
* **SAML 2.0** – Enterprise SAML integration with example Okta configuration

#### Amazon Cognito services
* **Amazon Cognito user pools** – Username and password authentication with hosted UI

#### Custom authentication
* **Developer provider** – Custom authentication system demonstration
* **Unauthenticated access** – Guest credentials for anonymous users

### Key features
* **Modal dialogs** – Educational content explaining flow differences and provider limitations
* **Flow decision guidance** – Interactive help for choosing the right authentication approach
* **Real-time results** – Live AWS credentials with proper expiration handling
* **Security best practices** – Server-side token exchange and secure credential handling
* **Comprehensive error handling** – User-friendly error messages and troubleshooting guidance


## Prerequisites

Before you begin, ensure you have the following:

* **An AWS account** with access to Amazon Cognito. If you do not have an AWS account, see [Getting started with AWS](https://aws.amazon.com/getting-started/) to sign up for an AWS account and create a user with administrative access
* **Python 3.8 or later** installed on your development machine
* **Git** installed for cloning the repository
* **AWS credentials configured** with appropriate permissions for making authenticated requests to AWS services. 
* **Developer accounts** for the identity providers you want to use (optional - you can start with one provider)

For detailed instructions on implementing AWS credentials and identity pool federation in your specific SDK, see the [Getting credentials documentation](https://docs.aws.amazon.com/cognito/latest/developerguide/getting-credentials.html).

## Quick Start

### Step 1. environment setup

1. Clone the repository and navigate to the web directory:
   ```bash
   git clone <repository-url>
   cd identity_pools_example_demo/web
   ```

2. Install the required dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Create a `.env` file based on the `.env.example` template:
   ```bash
   cp .env.example .env
   ```

### 2. Configure your environment

Update the `.env` file with your AWS region, Identity Pool ID, and social provider credentials

### Step 3: Run the application

1. Start the backend server:
   ```bash
   python backend/oauth_server.py
   ```
   
   You should see output similar to:
   ```
   Starting OAuth server on port 8006...
   Server is running at http://localhost:8006
   ```

2. In a new terminal, navigate to the frontend directory and start the frontend server:
   ```bash
   cd frontend
   python -m http.server 8001
   ```
   
   You should see output similar to:
   ```
   Serving HTTP on 0.0.0.0 port 8001 (http://0.0.0.0:8001/) ...
   ```

3. Open your web browser and navigate to `http://localhost:8001`

If successful, you should see the demo application interface with options to explore different authentication flows.

## Identity pool setup

### Create identity pool

1. Open [Amazon Cognito Console](https://console.aws.amazon.com/cognito/)
2. Choose **Identity pools** → **Create identity pool**
3. Configure:
   * **User access**: Enable both **Authenticated** and **Guest access**
   * **Identity sources**: Select providers you plan to use
4. Create IAM roles:
   * **Authenticated role**: `Cognito_IdentityPoolsDemo_Auth_Role`
   * **Guest role**: `Cognito_IdentityPoolsDemo_Unauth_Role`
5. **Important**: Under **Basic (classic) authentication**, select **Activate basic flow**
6. Note your **Identity Pool ID** and **Region**

### Configure identity providers

Add your configured providers to the identity pool:

1. In your identity pool, choose **Identity providers**
2. Configure each provider with the appropriate App ID/Client ID
3. For SAML: Add your SAML provider ARN
4. For OIDC: Add your OIDC provider URL

## Provider configuration

### Google OAuth 2.0

1. [Google Cloud Console](https://console.cloud.google.com/) → **Credentials**
2. Create **OAuth client ID** (Web application)
3. Add redirect URI: `http://localhost:8006/auth/google/callback`
4. Note Client ID and Client Secret

### Facebook login

1. [Facebook for Developers](https://developers.facebook.com/) → Create App
2. Add **Facebook Login** product
3. Add redirect URI: `http://localhost:8006/auth/facebook/callback`
4. Note App ID and App Secret

### SAML 2.0 (Example: Okta)

1. Okta Admin Console → Create SAML 2.0 app
2. Single Sign On URL: `http://localhost:8006/auth/saml/callback`
3. Configure attribute statements for AWS role mapping
4. AWS IAM Console → Create SAML identity provider
5. Upload Okta metadata document

### User pools

1. Cognito Console → **User pools** → Create or select pool
2. **App integration** → Configure Hosted UI
3. Callback URL: `http://localhost:8001/`
4. Note User Pool ID, App Client ID, and App Client Secret

## How it works

### Enhanced flow (recommended)
```
User Authentication → Provider Token → GetCredentialsForIdentity → AWS Credentials
```

**Benefits:**
- Single API call
- Simplified implementation
- Better performance
- Automatic role selection

### Basic Flow (Traditional)
```
User Authentication → Provider Token → GetId → GetOpenIdToken → AssumeRoleWithWebIdentity → AWS Credentials
```

**Benefits:**
- Granular control
- Custom role selection
- Token inspection capability
- Legacy compatibility

### Flow selection guidance

The application provides interactive guidance:

* **Enhanced flow**: Recommended for new applications, mobile apps, and when you want simplified implementation
* **Basic flow**: Choose when you need custom role selection logic or token inspection
* **SAML limitation**: SAML providers only work with enhanced flow due to automatic role selection requirements

## Architecture

```
Frontend (8001)              Backend (8006)               AWS Services
├── Interactive UI           ├── OAuth Server             ├── Cognito Identity Pools
├── Flow Demonstrations      ├── Token Exchange           ├── IAM Roles  
├── Educational Modals       ├── Security Handling        └── STS (Credentials)
└── Real-time Results        └── Provider Integration
```

## Security features

* **Server-side token exchange** – Client secrets never exposed to browser
* **Temporary credentials** – All AWS credentials have expiration times
* **CORS protection** – Proper cross-origin request handling
* **Error sanitization** – Sensitive information filtered from error messages

## Troubleshooting

### Common issues

**"Identity pool not found"**
- Verify `IDENTITY_POOL_ID` in `.env` file
- Check AWS region matches your identity pool

**"Provider not configured"**
- Ensure provider is added to identity pool in AWS Console
- Verify App ID/Client ID matches between provider and identity pool

**SAML "Basic flow not supported"**
- SAML only works with enhanced flow
- Modal dialog explains this limitation with documentation links

**OAuth callback errors**
- Verify callback URLs match exactly in provider developer portals
- Check that both servers are running (ports 8001 and 8006)

### Debug information

* Browser console shows detailed client-side logs
* Server terminal displays backend processing information
* API visualizer shows actual AWS API calls and responses

## What you'll learn

* **Flow Comparison** – When to use enhanced vs basic flow
* **Provider Integration** – Real OAuth 2.0, SAML, and OIDC implementations
* **Security Patterns** – Proper credential handling and token exchange
* **AWS API Usage** – Direct interaction with Cognito and STS APIs
* **Error Handling** – Production-ready error management
* **Decision Making** – Interactive guidance for architecture choices

## Production Considerations

Before deploying to production:

1. **HTTPS Configuration** – Enable SSL/TLS
2. **Environment Variables** – Use secure secret management
3. **CORS Configuration** – Restrict to your domain
4. **Error Handling** – Implement comprehensive logging
5. **Rate Limiting** – Add request throttling
6. **Monitoring** – Set up CloudWatch metrics

## Additional Resources

* [Amazon Cognito Identity Pools Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/identity-pools.html)
* [Authentication Flow Documentation](https://docs.aws.amazon.com/cognito/latest/developerguide/authentication-flow.html)
* [AWS SDK for Python (Boto3)](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
* [OAuth 2.0 Security Best Practices](https://tools.ietf.org/html/draft-ietf-oauth-security-topics)

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.