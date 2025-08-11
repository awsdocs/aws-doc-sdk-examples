# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
import urllib.parse
import requests
import boto3
import logging
import os
import base64
from typing import Dict, Optional

logger = logging.getLogger(__name__)

class OAuthFlowHandler:
    def __init__(self, cognito_config: dict, oauth_providers: dict, enhanced_flow=None, basic_flow=None):
        self.cognito_config = cognito_config
        self.oauth_providers = oauth_providers
        self.enhanced_flow = enhanced_flow
        self.basic_flow = basic_flow
    
    def handle_token_exchange(self, provider: str, code: str, redirect_uri: str) -> dict:
        """Extract the token exchange logic"""
        try:
            config = self.oauth_providers[provider]
            token_data = {
                'grant_type': 'authorization_code',
                'client_id': config['client_id'],
                'client_secret': config['client_secret'],
                'code': code,
                'redirect_uri': redirect_uri
            }
            
            logger.info(f"Exchanging token for {provider}")
            token_response = requests.post(config['token_url'], data=token_data, timeout=(10, 30))
            
            if token_response.status_code == 200:
                tokens = token_response.json()
                id_token = tokens.get('id_token')
                access_token = tokens.get('access_token')
                provider_token = id_token if id_token else access_token
                
                logger.info(f"Token exchange successful for {provider}")
                
                # Debug: Log the JWT token for OIDC
                if provider.lower() == 'oidc' and provider_token:
                    print(f"DEBUG: OIDC JWT Token: {provider_token[:50]}...")
                    # Decode and print issuer
                    try:
                        import base64
                        import json
                        parts = provider_token.split('.')
                        if len(parts) == 3:
                            payload = parts[1]
                            padding = len(payload) % 4
                            if padding:
                                payload += '=' * (4 - padding)
                            decoded = base64.urlsafe_b64decode(payload)
                            jwt_payload = json.loads(decoded.decode('utf-8'))
                            print(f"DEBUG: JWT Issuer: {jwt_payload.get('iss', 'NOT FOUND')}")
                            print(f"DEBUG: JWT Audience: {jwt_payload.get('aud', 'NOT FOUND')}")
                    except Exception as e:
                        print(f"DEBUG: Failed to decode JWT: {e}")
                
                return {
                    'success': True,
                    'token': provider_token,
                    'all_tokens': tokens,
                    'oauth_response': tokens,  # Include actual OAuth response for visualization
                    'token_endpoint': config['token_url'],
                    'provider_name': provider
                }
            else:
                logger.error(f"Token exchange failed for {provider}: {token_response.status_code}")
                return {
                    'success': False,
                    'error': f"Token exchange failed: {token_response.text}"
                }
        except requests.Timeout:
            logger.error(f"Timeout during token exchange for {provider}")
            return {'success': False, 'error': f"Request timeout for {provider}"}
        except requests.RequestException as e:
            logger.error(f"Network error during token exchange for {provider}: {e}")
            return {'success': False, 'error': f"Network error: {str(e)}"}
        except Exception as e:
            logger.critical(f"Unexpected error during token exchange for {provider}: {e}")
            return {'success': False, 'error': f"Token exchange error: {str(e)}"}
    
    def handle_enhanced_flow(self, provider: str, token: str) -> dict:
        """Extract enhanced flow logic"""
        try:
            if not self.enhanced_flow:
                return {'success': False, 'error': 'Enhanced flow not available'}
            
            logger.info(f"Using Enhanced Flow for {provider}")
            
            # Handle guest access
            if provider.lower() == 'guest':
                return self.enhanced_flow.enhanced_flow_guest()
            
            # Handle OIDC with proper issuer mapping per AWS docs
            if provider.lower() == 'oidc':
                # Use the issuer as the key for logins map (per AWS docs)
                oidc_issuer = os.getenv('OIDC_ISSUER', 'login.provider.com')
                # Use full issuer URL as provider key
                cognito_provider = oidc_issuer
                
                # Validate token format before sending to Cognito
                if not self._is_valid_jwt(token):
                    return self._get_oidc_setup_error()
            else:
                # Map other provider names to Cognito format
                provider_map = {
                    'google': 'Google',
                    'facebook': 'Facebook', 
                    'amazon': 'Amazon',
                    'apple': 'SignInWithApple'
                }
                cognito_provider = provider_map.get(provider.lower(), provider.capitalize())
            
            # For OIDC, pass 'OIDC' as provider type, not the domain
            flow_provider = 'OIDC' if provider.lower() == 'oidc' else provider.capitalize()
            result = self.enhanced_flow.enhanced_flow_authenticated(flow_provider, token)
            
            # Handle OIDC-specific errors
            if not result.get('success') and provider.lower() == 'oidc':
                error_msg = result.get('error', '')
                if 'Invalid login token' in error_msg or 'Not a valid OpenId Connect' in error_msg:
                    return self._get_oidc_setup_error()
            
            logger.info(f"Enhanced Flow result for {provider}: {result.get('success')}")
            return result
            
        except Exception as e:
            logger.critical(f"Enhanced Flow error for {provider}: {e}")
            return {'success': False, 'error': f"Enhanced Flow error: {str(e)}"}
    
    def handle_basic_flow(self, provider: str, token: str) -> dict:
        """Extract basic flow logic"""
        try:
            if not self.basic_flow:
                return {'success': False, 'error': 'Basic flow not available'}
            
            logger.info(f"Using Basic Flow for {provider}")
            
            # Handle guest access
            if provider.lower() == 'guest':
                return self.basic_flow.basic_flow_guest()
            
            # Handle OIDC with proper issuer mapping per AWS docs
            if provider.lower() == 'oidc':
                # Use the issuer as the key for logins map (per AWS docs)
                oidc_issuer = os.getenv('OIDC_ISSUER', 'login.provider.com')
                # Use full issuer URL as provider key
                cognito_provider = oidc_issuer
                
                # Validate token format before sending to Cognito
                if not self._is_valid_jwt(token):
                    return self._get_oidc_setup_error()
            else:
                # Map other provider names to BasicFlowDemo format
                provider_map = {
                    'google': 'Google',
                    'facebook': 'Facebook',
                    'amazon': 'Amazon'
                }
                cognito_provider = provider_map.get(provider.lower(), provider.capitalize())
            
            # For OIDC, pass 'OIDC' as provider type, not the domain
            flow_provider = 'OIDC' if provider.lower() == 'oidc' else provider.capitalize()
            result = self.basic_flow.basic_flow_authenticated(flow_provider, token)
            
            # Handle OIDC-specific errors
            if not result.get('success') and provider.lower() == 'oidc':
                error_msg = result.get('error', '')
                if 'Invalid login token' in error_msg or 'Not a valid OpenId Connect' in error_msg:
                    return self._get_oidc_setup_error()
            
            logger.info(f"Basic Flow result for {provider}: {result.get('success')}")
            return result
            
        except Exception as e:
            logger.critical(f"Basic Flow error for {provider}: {str(e)}")
            return {'success': False, 'error': f"Basic Flow error: {str(e)}"}
    
    def _is_valid_jwt(self, token):
        """Basic JWT format validation"""
        try:
            parts = token.split('.')
            return len(parts) == 3  # header.payload.signature
        except:
            return False
    
    def handle_saml_flow(self, saml_response: str) -> dict:
        """Handle SAML authentication flow"""
        try:
            if not self.enhanced_flow:
                return {'success': False, 'error': 'Enhanced flow not available'}
            
            # Use the full SAML response directly - Cognito expects the full base64 encoded response
            logger.info("Using Enhanced Flow for SAML")
            result = self.enhanced_flow.enhanced_flow_authenticated('SAML', saml_response)
            
            if not result.get('success'):
                error_msg = result.get('error', '')
                if 'configuration missing' in error_msg.lower():
                    return self._get_saml_setup_error()
            
            return result
            
        except Exception as e:
            logger.error(f"SAML Flow error: {e}")
            return {'success': False, 'error': f"SAML Flow error: {str(e)}"}
    
    def _get_oidc_setup_error(self):
        """Return user-friendly OIDC setup error message"""
        return {
            'success': False,
            'error': 'OIDC provider setup required',
            'details': 'To use OIDC authentication, you need: 1) A real OIDC provider (Auth0, Okta, etc.), 2) IAM OIDC Identity Provider configured in AWS, 3) Valid environment variables (OIDC_ISSUER, OIDC_CLIENT_ID, OIDC_CLIENT_SECRET)',
            'error_type': 'configuration_error'
        }
    

    def _extract_saml_assertion(self, saml_response: str) -> Optional[str]:
        """Extract SAML assertion from SAML response"""
        try:
            decoded_response = base64.b64decode(saml_response).decode('utf-8')
            root = ET.fromstring(decoded_response)
            
            namespaces = {
                'saml': 'urn:oasis:names:tc:SAML:2.0:assertion',
                'samlp': 'urn:oasis:names:tc:SAML:2.0:protocol'
            }
            
            assertion = root.find('.//saml:Assertion', namespaces)
            if assertion is not None:
                assertion_str = ET.tostring(assertion, encoding='unicode')
                return base64.b64encode(assertion_str.encode('utf-8')).decode('utf-8')
            
            return None
        except Exception as e:
            logger.error(f"Failed to extract SAML assertion: {e}")
            return None
    
    def _get_saml_setup_error(self):
        """Return user-friendly SAML setup error message"""
        return {
            'success': False,
            'error': 'SAML provider setup required',
            'details': 'Required: 1) AWS_ACCOUNT_ID and SAML_PROVIDER in .env file, 2) SAML Identity Provider created in AWS IAM, 3) Identity pool configured with SAML provider',
            'error_type': 'configuration_error'
        }
