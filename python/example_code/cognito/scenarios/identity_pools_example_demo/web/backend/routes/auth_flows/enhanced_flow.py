# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Demonstrates the Enhanced Authentication Flow for Amazon Cognito Identity Pools.
"""
import boto3
import logging
from botocore.exceptions import ClientError
from backend.config.cognito_config import COGNITO_CONFIG

from backend.src.identity_pool_manager import IdentityPoolManager

logger = logging.getLogger(__name__)

class EnhancedFlowDemo:

    """
    The Enhanced Flow combines the GetId and GetCredentialsForIdentity operations into a single call. This provides
    a more efficient way to obtain AWS credentials. This flow is recommended over the Basic Flow as it reduces API calls and complexity.

    For complete usage examples, see: examples/enhanced_flow_examples.py

    Key features of the Enhanced Flow:
    - Simplified API calls (GetId + GetCredentialsForIdentity)
    - Supports unauthenticated (guest) access
    - Supports authenticated access via various providers (Social, OIDC, SAML, Cognito User Pools, Developer Authenticated)
    - Automatic role selection based on the identity pool configuration
    """
    
    # Provider mapping constants
    SOCIAL_PROVIDERS = {
        'Google': 'accounts.google.com',
        'Facebook': 'graph.facebook.com',
        'Amazon': 'www.amazon.com',
    }
    
    OIDC_DEFAULT_PROVIDER = 'samples.auth0.com'
    
    def __init__(self, cognito_identity_client=None):
        self.identity_manager = IdentityPoolManager()
        self.cognito_identity = cognito_identity_client or boto3.client(
            'cognito-identity', 
            region_name=COGNITO_CONFIG['REGION']
        )
        
    def get_id(self, logins=None):
        """
        Gets an Identity ID from Amazon Cognito.
        
        Args:
            logins (dict, optional): Provider tokens for authenticated access
        
        Returns:
            str: Identity ID on success
        
        Raises:
            ClientError: If the API call fails
        """
        try:
            params = {'IdentityPoolId': self.identity_manager.identity_pool_id}
            if logins is not None:
                params['Logins'] = logins
            response = self.cognito_identity.get_id(**params)
            return response['IdentityId']
        except ClientError as error:
            logger.error(f"Failed to get identity ID: {error}")
            raise

    def get_credentials_for_identity(self, identity_id, logins=None):
        """
        Gets AWS credentials for an Identity ID.
        
        Args:
            identity_id (str): The Identity ID
            logins (dict, optional): Provider tokens for authenticated access
        
        Returns:
            dict: AWS credentials with AccessKeyId, SecretKey, SessionToken, Expiration
        
        Raises:
            ClientError: If the API call fails
        """
        try:
            params = {'IdentityId': identity_id}
            if logins is not None:
                params['Logins'] = logins
            
            response = self.cognito_identity.get_credentials_for_identity(**params)
            credentials = response['Credentials']
        
            if 'Expiration' in credentials:
                credentials['Expiration'] = credentials['Expiration'].isoformat()
            return credentials
        except ClientError as error:
            logger.error(f"Failed to get credentials: {error}")
            raise

    def enhanced_flow_guest(self):
        """
        Implements guest (unauthenticated) access using Enhanced Flow.

        This method:
        1. Gets an identity ID without authentication
        2. Gets AWS credentials with unauthenticated role
        3. Returns credentials that can be used for AWS API calls 

        Returns:
            dict: Authentication result containing identity ID and credentials.
        
        Use Case: Public content access, analytics collection
        """
        try:
            print("Handling request for Enhanced Flow - Guest access")
            logger.info("Starting Enhanced Flow - Guest Access")

            # Step 1: Get identity ID without authentication
            print("Step 1: Calling GetId() API...")
            print(f"  → Request: {{\"IdentityPoolId\": \"{self.identity_manager.identity_pool_id}\"}}")
            identity_id = self.get_id()
            print(f"  → Response: Identity ID: {identity_id}")
            logger.info(f"Got Guest Identity ID: {identity_id}")
            
            # Step 2: Get AWS credentials with unauthenticated role
            print("Step 2: Calling GetCredentialsForIdentity() API...")
            print(f"  → Request: {{\"IdentityId\": \"{identity_id}\"}}")
            credentials = self.get_credentials_for_identity(identity_id)
            print(f"  → Response: AWS credentials obtained (AccessKeyId: {credentials['AccessKeyId'][:8]}..., expires in 1 hour)")
            print("✓ Enhanced Flow completed successfully")
            logger.info(f"Got credentials:{credentials is not None}")

            return {
                'success': True,
                'flow_type': 'enhanced_guest',
                'provider': 'Guest',
                'identity_id': identity_id,
                'credentials': credentials
            }
        except Exception as error:
            print(f"✗ Enhanced Flow failed: {str(error)}")
            logger.error(f"Guest access failed: {str(error)}")
            return {
                'success': False,
                'error': str(error)
            }
    
    def enhanced_flow_authenticated(self, provider_type, provider_token):
        """
        Implements authenticated access using Enhanced Flow.

        Args:
            provider_type (str): Identity provider (e.g., 'Google', 'UserPool', 'OIDC')
            provider_token (str): Authentication token from the provider

        Returns:
            dict: Authentication result with success, identity_id, credentials
        """
        try:
            # Handle Developer authentication separately (uses different API)
            if provider_type == 'Developer':
                 return self.enhanced_flow_developer_authenticated(provider_token)
            
            # Handle OIDC providers dynamically
            if provider_type == 'OIDC':
                # Use the actual OIDC issuer from environment
                import os
                oidc_issuer = os.getenv('OIDC_ISSUER', 'https://samples.auth0.com')
                # Use the issuer exactly as configured - user must match their Identity Pool format
                provider_name = oidc_issuer
            elif provider_type in self.SOCIAL_PROVIDERS:
                provider_name = self.SOCIAL_PROVIDERS[provider_type]
            elif provider_type in ('UserPool', 'Userpool'):
                provider_name = f"cognito-idp.{COGNITO_CONFIG['REGION']}.amazonaws.com/{COGNITO_CONFIG['USER_POOL_ID']}"
            elif provider_type == 'SAML':
                account_id = COGNITO_CONFIG.get('ACCOUNT_ID')
                saml_provider = COGNITO_CONFIG.get('SAML_PROVIDER')
                
                if not account_id or not saml_provider:
                    return {
                        'success': False,
                        'error': 'SAML configuration missing: AWS_ACCOUNT_ID and SAML_PROVIDER required in .env file'
                    }
                
                provider_name = f'arn:aws:iam::{account_id}:saml-provider/{saml_provider}'
                logger.info(f"Using SAML provider ARN: {provider_name}")
            else:
                raise ValueError(f"Unsupported provider: {provider_type}")
            
            logins = {provider_name: provider_token}
            
            # Standard flow for all providers including SAML
            identity_id = self.get_id(logins)
            logger.info(f"Got identity ID: {identity_id}")
            
            credentials = self.get_credentials_for_identity(identity_id, logins)

            # Get the OpenID token for display (optional for SAML)
            openid_token = None
            if provider_type != 'SAML':
                try:
                    token_response = self.cognito_identity.get_open_id_token(
                        IdentityId=identity_id,
                        Logins=logins
                    )
                    openid_token = token_response.get('Token')
                except Exception as token_error:
                    logger.warning(f"Could not get OpenID token: {token_error}")
                    openid_token = None
            
            return {
                'success' : True,
                'flow_type': 'enhanced_authenticated',
                'provider': provider_type,
                'identity_id': identity_id,
                'credentials': credentials,
                'openid_token': openid_token,
                # Enhanced data for frontend display
                'provider_key': provider_name,
                'provider_token': provider_token,
                'account_id': COGNITO_CONFIG.get('ACCOUNT_ID', '123456789012'),
                'identity_pool_id': self.identity_manager.identity_pool_id,
                'region': COGNITO_CONFIG['REGION']
            }
        except Exception as error:
            logger.error(f"Enhanced flow failed for {provider_type}: {str(error)}")
            return {
                'success': False,
                'error': str(error)
            }
            
    def enhanced_flow_developer_authenticated(self, developer_user_identifier):
        """
        Implements the Enhanced Flow for Developer authenticated identities.
        
        Args:
            developer_user_identifier (str): User identifier from your authentication system
        
        Returns:
            dict: Contains identity ID and AWS credentials
        """
        try:
            import os
            
            # Check if AWS credentials are available
            if not os.getenv('AWS_ACCESS_KEY_ID') or not os.getenv('AWS_SECRET_ACCESS_KEY'):
                return {
                    'success': False,
                    'error': 'Developer authentication requires AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in .env file'
                }
            
            # Create Cognito client with explicit credentials
            cognito_client = boto3.client(
                'cognito-identity',
                region_name=COGNITO_CONFIG['REGION'],
                aws_access_key_id=os.getenv('AWS_ACCESS_KEY_ID'),
                aws_secret_access_key=os.getenv('AWS_SECRET_ACCESS_KEY')
            )
            
            # Step 1: GetOpenIdTokenForDeveloperIdentity
            token_response = cognito_client.get_open_id_token_for_developer_identity(
                IdentityPoolId=self.identity_manager.identity_pool_id,
                Logins={
                    COGNITO_CONFIG.get('DEVELOPER_PROVIDER_NAME', 'MyDeveloperProvider'): developer_user_identifier
                }
            )
            
            identity_id = token_response['IdentityId']
            token = token_response['Token']
            
            # Step 2: GetCredentialsForIdentity - Exchange the token for AWS credentials
            credentials_response = cognito_client.get_credentials_for_identity(
                IdentityId=identity_id,
                Logins={
                    'cognito-identity.amazonaws.com': token
                }
            )
            
            credentials = credentials_response['Credentials']
            if 'Expiration' in credentials:
                credentials['Expiration'] = credentials['Expiration'].isoformat()
            
            return {
                'success': True,
                'flow_type': 'enhanced_developer',
                'provider': 'Developer',
                'identity_id': identity_id,
                'credentials': credentials,
                'openid_token': token,
                'provider_key': COGNITO_CONFIG.get('DEVELOPER_PROVIDER_NAME', 'MyDeveloperProvider'),
                'provider_token': developer_user_identifier
            }
        except Exception as error:
            logger.error(f"Developer authentication failed: {error}")
            return {
                'success': False,
                'error': str(error)
            }
        
    def get_credentials_with_provider(self, logins=None):
        """
        Combines getting identity ID and credentials in one call.
        
        Args:
            logins (dict, optional): Provider tokens
        
        Returns:
            dict: Contains identity_id and credentials
        """
        try:
            identity_id = self.get_id(logins)
            credentials = self.get_credentials_for_identity(identity_id, logins)
            return {
                'identity_id': identity_id,
                'credentials': credentials
            }
        except Exception as error:
            logger.error(f"Failed to get credentials with provider: {str(error)}")
            raise

def run_enhanced_flow_demo():
    """
    Shows how to use Enhanced Flow authentication with Amazon Cognito Identity Pools.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    demo = EnhancedFlowDemo()
    
    print("Running Amazon Cognito Identity Pools Enhanced Flow authentication demonstration.")
    
    try:
        # Demo 1: Guest Access
        print("\nDemo 1: Guest (Unauthenticated) Access")
        result = demo.enhanced_flow_guest()
        if result['success']:
            print(f"Guest Identity ID: {result['identity_id']}")
            print(f"Credentials obtained successfully")
        else:
            print(f"Guest access failed: {result['error']}")
            
        # Demo 2: Social Provider Authentication
        print("\nDemo 2: Social Provider Authentication")
        for provider in demo.SOCIAL_PROVIDERS.keys():
            result = demo.enhanced_flow_authenticated(
                provider_type=provider,
                provider_token=f'SAMPLE_{provider.upper()}_TOKEN'
            )
            if result['success']:
                print(f"\n{provider} Authentication:")
                print(f"Identity ID: {result['identity_id']}")
                print(f"Credentials obtained successfully")
            else:
                print(f"\n{provider} Authentication failed: {result['error']}")
                
        # Demo 3: Additional Provider Types
        additional_demos = [
            ('UserPool', 'SAMPLE-ID-TOKEN', 'Cognito User Pool'),
            ('Developer', 'SAMPLE-DEVELOPER-USER-ID', 'Developer Authenticated')
        ]
        
        for provider_type, token, description in additional_demos:
            print(f"\nDemo: {description} Authentication")
            if provider_type == 'Developer':
                result = demo.enhanced_flow_developer_authenticated(token)
            else:
                result = demo.enhanced_flow_authenticated(provider_type, token)
            
            if result['success']:
                print(f"Identity ID: {result['identity_id']}")
                print(f"Credentials obtained successfully")
            else:
                print(f"Authentication failed: {result['error']}")
            
    except Exception as error:
        print(f"Demo failed: {str(error)}")

if __name__ == '__main__':
    run_enhanced_flow_demo()
