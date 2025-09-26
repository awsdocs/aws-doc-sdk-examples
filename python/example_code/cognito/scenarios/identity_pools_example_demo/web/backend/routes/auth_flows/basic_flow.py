# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
[Ongoing] Demonstrates the Basic (Classic) Flow authentication pattern for Amazon Cognito Identity Pools:
GetId → GetOpenIdToken → AssumeRoleWithWebIdentity
"""
import logging
import time
from botocore.exceptions import ClientError
import boto3
from backend.src.identity_pool_manager import IdentityPoolManager
from backend.config.cognito_config import COGNITO_CONFIG
from backend.routes.auth_flows.enhanced_flow import EnhancedFlowDemo

logger = logging.getLogger(__name__)

class BasicFlowDemo:
    """
    Demonstrates Basic authentication flow for Amazon Cognito Identity Pools.

    For complete usage examples, see: examples/basic_flow_examples.py
    """
    
    # Provider mapping constants
    PROVIDER_FORMATS = {
        'Google': 'accounts.google.com',
        'Facebook': 'graph.facebook.com',
        'Amazon': 'www.amazon.com',
        'Twitter': 'api.twitter.com',
        'OIDC': None,  # OIDC uses issuer domain as key
        'Developer': 'cognito-identity.amazonaws.com'
    }
    
    def __init__(self):
        """Initialize the Basic Flow demo with an identity pool manager."""
        self.identity_manager = IdentityPoolManager() # handles core operation
        self.cognito_identity = boto3.client('cognito-identity', region_name=COGNITO_CONFIG['REGION'])
        self.sts = boto3.client('sts', region_name=COGNITO_CONFIG['REGION']) # Boto3 client interact with AWS services
        
        # Session duration for credential lifetimes
        self._session_duration = 3600 # 1 hour default
    
        # Flow state for fallback handling
        self.current_flow = None
        self.current_provider_token =  None
        self.current_provider_type = None

    def _validate_session_duration(self, duration_seconds):
        """Validates the session duration is within allowed limits (15 minutes to 12 hours)"""
        min_duration = 900 # 15 minutes
        max_duration = 43200 # 12 hours

        if not min_duration <= duration_seconds <= max_duration:
            raise ValueError(
                f"Session duration must be between {min_duration} and {max_duration} seconds"
        )
    def _check_role_mappings(self):
        """
        Verifies that the identity pool doesn't have role mappings configured.
        Basic flow cannot be used with role mappings.
        """
        try:
            response = self.cognito_identity.get_identity_pool_roles(
                IdentityPoolId=self.identity_manager.identity_pool_id
            )
            print(f"Identity pool roles response: {response}")
            
            # Check if the identity pool has role mappings
            has_role_mappings = 'RoleMappings' in response and response['RoleMappings']
            
            # Check if the identity pool has authenticated and unauthenticated roles
            has_auth_roles = 'Roles' in response and 'authenticated' in response['Roles']
            has_unauth_roles = 'Roles' in response and 'unauthenticated' in response['Roles']
            
            if has_role_mappings:
                logger.warning("Role mappings detected. Falling back to Enhanced Flow (recommended)")
                print("Role mappings detected in identity pool - Basic Flow cannot be used")
                enhanced_flow = EnhancedFlowDemo()

                if self.current_flow == 'guest':
                    result = enhanced_flow.enhanced_flow_guest()
                else:
                    result = enhanced_flow.enhanced_flow_authenticated(
                        self.current_provider_type,
                        self.current_provider_token
                    )
            
                # Add educational message about Basic Flow requirements
                result['fallback_reason'] = 'Role mappings detected - Basic Flow requires identity pools without role mappings'
                result['basic_flow_requirements'] = [
                    'Identity pool must not have role mappings configured',
                    'AWS credentials must be properly configured',
                    'Identity pool must have both authenticated and unauthenticated roles assigned'
                ]
                return result
            
            # Check if the identity pool has the required roles
            if not has_auth_roles or not has_unauth_roles:
                logger.warning("Missing required roles. Basic Flow requires both authenticated and unauthenticated roles.")
                print("Identity pool missing required roles - Basic Flow requires both authenticated and unauthenticated roles")
                return {
                    'success': False,
                    'error': 'Basic Flow requires both authenticated and unauthenticated roles to be configured in the identity pool',
                    'basic_flow_requirements': [
                        'Identity pool must have both authenticated and unauthenticated roles assigned'
                    ]
                }
                
            # No role mappings and has required roles, continue with Basic Flow
            print("Identity pool configuration supports Basic Flow")
            return None 
        except ClientError as error:
            logger.warning("Cannot verify role mappings, falling back to Enhanced Flow: %s", 
                        error.response['Error']['Message'])
            print(f"Error checking identity pool configuration: {error}")
            enhanced_flow = EnhancedFlowDemo()
    
            if self.current_flow == 'guest':
                result = enhanced_flow.enhanced_flow_guest()
            else:
                result = enhanced_flow.enhanced_flow_authenticated(
                    self.current_provider_type,
                    self.current_provider_token
                )
            # Message about basic flow requirements
            result['fallback_reason'] = 'AWS credential configuration issue - Basic Flow requires proper AWS setup'
            result['basic_flow_requirements'] = [
                'AWS credentials must be properly configured with cognito-identity:GetIdentityPoolRoles permission',
                'Identity pool must not have role mappings configured', 
                'Identity pool must have both authenticated and unauthenticated roles assigned'
            ]
            return result

    
    def basic_flow_guest(self):
        """
        Run Basic Flow for guest (unauthenticated) access.
        Falls back to Enhanced Flow if role mappings are detected.

        Returns:
            dict: Contains identity ID and temporary credentials information
        """
        print("Handling request for Basic Flow - Guest access")
        logger.info("Starting Basic Flow - Guest (UnAuthenticated) Access")
        
        # Store current flow information for enhanced flow fallback
        self.current_flow = 'guest'
        self.current_provider_type = None
        self.current_provider_token = None
        try: 
            # Check for role mappings first
            fallback_result = self._check_role_mappings()
            if fallback_result:
                return fallback_result

            # Step 1: GetId - Get a unique identifier for unauthenticated user
            print("Step 1: Calling GetId() API...")
            print(f"  → Request: {{\"IdentityPoolId\": \"{self.identity_manager.identity_pool_id}\"}}")
            identity_id = self.identity_manager.get_id()
            print(f"  → Response: Identity ID: {identity_id}")
            logger.info("Got Identity ID: %s", identity_id)
            
            # Step 2: GetOpenIdToken - Exchange identity ID for OpenID token
            print("Step 2: Calling GetOpenIdToken() API...")
            print(f"  → Request: {{\"IdentityId\": \"{identity_id}\"}}")
            open_id_token = self.identity_manager.get_open_id_token(identity_id)
            print(f"  → Response: OpenID token obtained (length: {len(open_id_token)} chars)")
            logger.info("Got OpenID Token")

            # Validate duration
            self._validate_session_duration(duration_seconds=3600)
            
            # Step 3: AssumeRoleWithWebIdentity - Exchange token for AWS credentials
            print("Step 3: Calling AssumeRoleWithWebIdentity() API...")
            print(f"  → Request: {{\"RoleArn\": \"{self.identity_manager.unauthenticated_role_arn}\", \"WebIdentityToken\": \"[JWT_TOKEN]\"}}")
            credentials = self.identity_manager.assume_role_with_web_identity(
                role_arn = self.identity_manager.unauthenticated_role_arn,
                role_session_name = f"basic-guest-{int(time.time())}",
                web_identity_token = open_id_token,
                duration_seconds=3600  # 1 hour (Session duration in seconds (15 - 720 minutes))
            )
            print(f"  → Response: AWS credentials obtained (AccessKeyId: {credentials['AccessKeyId'][:8]}..., expires in 1 hour)")
            print("✓ Basic Flow completed successfully")

            return {
                'flow_type': 'basic_guest',
                'provider': 'Guest',
                'identity_id': identity_id,
                'open_id_token': open_id_token,
                'credentials': {
                    'AccessKeyId': credentials['AccessKeyId'],
                    'SecretAccessKey': credentials['SecretAccessKey'],
                    'SessionToken': credentials['SessionToken'],
                    'Expiration': credentials['Expiration'].isoformat()
                },
                'success': True
            }
        except ClientError as error:
            print(f"✗ Basic Flow failed: {error.response['Error']['Message']}")
            logger.error(
                "Guest flow failed: %s",
                error.response['Error']['Message']
            )
            return {
                'success': False,
                'error': error.response['Error']
            }

    def basic_flow_authenticated(self, provider_type, provider_token):
        """
        Run Basic Flow for authenticated access with various providers.

        Args:
            provider_type: Type of provider('Google', 'Facebook', 'UserPool')
            provider_token: Authentication token from the provider
        
        Returns:
            dict: Results containing identity ID, provider information, and temporary credentials
        """

        # Store current flow information for enhanced flow fallback
        self.current_flow = 'authenticated'
        self.current_provider_type = provider_type
        self.current_provider_token = provider_token

        try: 
            # Check for role mappings first 
            fallback_result = self._check_role_mappings()
            if fallback_result:
                return fallback_result

            # Basic token validation
            if not provider_token:
                raise ValueError(f"Provider token is required for {provider_type}")

            # Get provider name from constants or dynamic mapping
            if provider_type == 'Developer':
                provider_name = COGNITO_CONFIG.get('DEVELOPER_PROVIDER_NAME')
            elif provider_type in self.PROVIDER_FORMATS:
                provider_name = self.PROVIDER_FORMATS[provider_type]
                # Handle OIDC provider mapping
                if provider_type == 'OIDC':
                    import os
                    oidc_issuer = os.getenv('OIDC_ISSUER', '')
                    provider_name = oidc_issuer  # Use full issuer URL as provider key
            elif provider_type in ('UserPool', 'Userpool'):
                provider_name = f'cognito-idp.{COGNITO_CONFIG["REGION"]}.amazonaws.com/{COGNITO_CONFIG["USER_POOL_ID"]}'
            elif provider_type == 'SAML':
                provider_name = f'arn:aws:iam::{COGNITO_CONFIG.get("ACCOUNT_ID")}:saml-provider/{COGNITO_CONFIG.get("SAML_PROVIDER")}'
            else:
                raise ValueError(f"Unsupported provider type: {provider_type}. Must be one of: {list(self.PROVIDER_FORMATS.keys()) + ['UserPool', 'SAML']}")
            logger.info(f"Starting Basic Flow - %s Authentication", provider_type)
        
            # Create logins map based on provider token
            logins = {provider_name: provider_token}
            
            # Special handling for Developer authentication
            if provider_type == 'Developer':
                # Developer authentication uses GetOpenIdTokenForDeveloperIdentity
                identity_id, open_id_token = self.identity_manager.get_open_id_token_for_developer_identity(logins)
                logger.info(f"Got Developer Identity ID: %s", identity_id)
                logger.info(f"Got Developer OpenID Token")
            else:
                # Regular flow for other providers
                # Step 1: GetId - Get a unique Identity ID with provider token for authenticated user
                identity_id = self.identity_manager.get_id(logins)
                logger.info(f"Got Identity ID: %s", identity_id)
                
                # Step 2: GetOpenIdToken - Exchange identity ID for OpenID token with logins
                open_id_token = self.identity_manager.get_open_id_token(identity_id, logins)
                logger.info(f"Got OpenID Token: %s", open_id_token)
            
            # Validate session duration
            self._validate_session_duration(self._session_duration)

            # Step 3: AssumeRoleWithWebIdentity - Exchange token for AWS credentials
            credentials = self.identity_manager.assume_role_with_web_identity(
                role_arn=self.identity_manager.authenticated_role_arn,
                role_session_name=f"basic-auth-{provider_type.lower()}-{int(time.time())}",
                web_identity_token=open_id_token,
                duration_seconds=3600
            )
            
            return {
                'flow_type': 'basic_authenticated',
                'provider': provider_type,
                'identity_id': identity_id,
                'open_id_token': open_id_token,
                'credentials': {
                    'AccessKeyId': credentials['AccessKeyId'],
                    'SecretAccessKey': credentials['SecretAccessKey'],
                    'SessionToken': credentials['SessionToken'],
                    'Expiration': credentials['Expiration'].isoformat()
                },
                'provider_key': provider_name,
                'success': True
            }
        except (ClientError, ValueError) as error:
            logger.error(
                "Authentication failed; %s", 
                str(error)
            )
            return {
                'success': False,
                'error': str(error)
            }
        
def run_basic_flow_demo():
    """
    Shows how to use Basic Flow authentication with Amazon Cognito Identity Pools.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    demo = BasicFlowDemo()

    print("Running Amazon Cognito Identity Pools Basic Flow authentication demonstration. ")

    try: 
        # Demo 1: Demonstrate guest (unauthenticated) access
        print("Demo 1: Demonstrating guest (unauthenticated) access: ")
        result = demo.basic_flow_guest()
        if result['success']:
            print(f"Guest Identity ID: {result['identity_id']}")
            print(f"Credentials expire: {result['credentials']['Expiration']}")
        else:
            print(f"Guest access failed: {result['error']}")
            
        
        # Demo 2: Social Provider Demo
        print("\n Demo 2: Demonstrating authenticated access with social providers: ")
        for provider in ['Google', 'Facebook', 'Amazon']:
            result = demo.basic_flow_authenticated(
                provider,
                f'SAMPLE_{provider.upper()}_TOKEN',
            )
            if result['success']:
                print(f"Authenticated with {provider} successful")
                print(f"Identity ID: {result['identity_id']}")
                print(f"Credentials expire: {result['credentials']['Expiration']}")
            else:
                print(f"Authenticated with {provider} failed: {result['error']}")
        
       # Demo 3: User pool demo
        print("\n Demo 3: Demonstrating authenticated access with Amazon Cognito User Pool: ")
        result = demo.basic_flow_authenticated(
            provider_type='UserPool',
            provider_token='SAMPLE-ID-TOKEN'
        )
        if result['success']:
            print(f"Authenticated with Cognito User Pool successful")
            print(f"Identity ID: {result['identity_id']}")
            print(f"Credentials expire: {result['credentials']['Expiration']}") 
        else:
            print(f"Authenticated access with Cognito User Pool failed: {result['error']}")
    except Exception as error:
        print(f"Demo failed: {str(error)}")

if __name__ == "__main__":
    run_basic_flow_demo()