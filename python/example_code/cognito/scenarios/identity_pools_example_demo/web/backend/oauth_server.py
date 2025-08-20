# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Generic OAuth server using standard OAuth 2.0 library
"""
import json
import urllib.parse
import requests
import sys
import os
import logging
import boto3
import base64
from dotenv import load_dotenv

# Focus on authentication flow progress, configuration issues, and failures
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    stream=sys.stdout,
    force=True
)

logging.getLogger('botocore').setLevel(logging.WARNING)
logging.getLogger('urllib3').setLevel(logging.WARNING)
logging.getLogger('boto3').setLevel(logging.WARNING)

# # Ensure oauth_handlers logs are visible for authentication flow tracking
# oauth_handler_logger = logging.getLogger('backend.oauth_handlers')
# oauth_handler_logger.setLevel(logging.INFO)

# Application logger
logger = logging.getLogger(__name__)
logger.info("OAuth server starting - authentication flows ready")

def is_custom_domain(domain):
    """Check if domain is custom (contains dots) or prefix (alphanumeric only)"""
    return domain and '.' in domain

def build_token_url(domain, region):
    """Build token URL based on domain type"""
    if is_custom_domain(domain):
        return f'https://{domain}/oauth2/token'
    else:
        return f'https://{domain}.auth.{region}.amazoncognito.com/oauth2/token'

load_dotenv()
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs

# Add path for enhanced flow import
backend_path = os.path.dirname(os.path.abspath(__file__))
web_path = os.path.dirname(backend_path)
sys.path.append(web_path)

from backend.oauth_handlers import OAuthFlowHandler

try:
    from backend.routes.auth_flows.enhanced_flow import EnhancedFlowDemo
    from backend.routes.auth_flows.basic_flow import BasicFlowDemo
    from backend.config.env_config import FRONTEND_CONFIG
    from backend.config.cognito_config import COGNITO_CONFIG
    ENHANCED_FLOW_AVAILABLE = True
    BASIC_FLOW_AVAILABLE = True
    logger.info("Enhanced flow and basic flow imported successfully")
except ImportError as e:
    try:
        from routes.auth_flows.enhanced_flow import EnhancedFlowDemo
        from routes.auth_flows.basic_flow import BasicFlowDemo
        from config.env_config import FRONTEND_CONFIG
        from config.cognito_config import COGNITO_CONFIG
        ENHANCED_FLOW_AVAILABLE = True
        BASIC_FLOW_AVAILABLE = True
        logger.info("Enhanced flow and basic flow imported successfully")
    except ImportError as e2:
        logger.error(f"Flow modules not available: {e2}")
        ENHANCED_FLOW_AVAILABLE = False
        BASIC_FLOW_AVAILABLE = False
        FRONTEND_CONFIG = {}
        COGNITO_CONFIG = {}

# Generic OAuth configuration for different providers
OAUTH_PROVIDERS = {
    'google': {
        'client_id': os.getenv('GOOGLE_CLIENT_ID', ''),
        'client_secret': os.getenv('GOOGLE_CLIENT_SECRET', ''),
        'auth_url': 'https://accounts.google.com/o/oauth2/v2/auth',
        'token_url': 'https://oauth2.googleapis.com/token',
        'scope': 'openid email profile'
    },
    'amazon': {
        'client_id': os.getenv('AMAZON_CLIENT_ID', ''),
        'client_secret': os.getenv('AMAZON_CLIENT_SECRET', ''),
        'auth_url': 'https://www.amazon.com/ap/oa',
        'token_url': 'https://api.amazon.com/auth/o2/token',
        'scope': 'profile',
        'fallback_client_id': os.getenv('AMAZON_FALLBACK_CLIENT_ID', '')
    },
    'facebook': {
        'client_id': os.getenv('FACEBOOK_APP_ID', ''),
        'client_secret': os.getenv('FACEBOOK_APP_SECRET', ''),
        'auth_url': 'https://www.facebook.com/v18.0/dialog/oauth',
        'token_url': 'https://graph.facebook.com/v18.0/oauth/access_token',
        'scope': 'public_profile'
    },
    'saml': {
        'auth_url': os.getenv('SAML_SSO_URL', 'https://your-saml-provider.com/sso'),
        'callback_url': f"{os.getenv('APP_URL', 'http://localhost:8006')}/auth/saml/callback",
        'entity_id': os.getenv('SAML_ENTITY_ID', 'urn:amazon:cognito:sp:your-identity-pool-id')
    },
    'oidc': {
        'client_id': os.getenv('OIDC_CLIENT_ID', ''),
        'client_secret': os.getenv('OIDC_CLIENT_SECRET', ''),
        'auth_url': os.getenv('OIDC_AUTHORIZATION_ENDPOINT', ''),
        'token_url': os.getenv('OIDC_TOKEN_ENDPOINT', ''),
        'scope': 'openid email profile',
        'issuer': os.getenv('OIDC_ISSUER', '')
    },
}

class GenericOAuthHandler(BaseHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        # Create flow instances only if available
        enhanced_flow = None
        basic_flow = None
        
        if ENHANCED_FLOW_AVAILABLE:
            try:
                enhanced_flow = EnhancedFlowDemo()
            except:
                pass
                
        if BASIC_FLOW_AVAILABLE:
            try:
                basic_flow = BasicFlowDemo()
            except:
                pass
        
        self.oauth_handler = OAuthFlowHandler(
            COGNITO_CONFIG, 
            OAUTH_PROVIDERS,
            enhanced_flow,
            basic_flow
        )
        
        super().__init__(*args, **kwargs)
    
    def add_cors_headers(self):
        frontend_url = os.getenv('FRONTEND_URL', 'http://localhost:8001')
        origin = self.headers.get('Origin')
        if origin == frontend_url:
            self.send_header('Access-Control-Allow-Origin', origin)
    
    def send_error_redirect(self, error_msg):
        error_param = urllib.parse.quote(error_msg)
        frontend_url = os.getenv('FRONTEND_URL', 'http://localhost:8001')
        self.send_response(302)
        self.send_header('Location', f'{frontend_url}/?error={error_param}')
        self.end_headers()
    
    def send_success_redirect(self, provider, result):
        if result.get('success'):
            flow_param = '&flow=basic' if result.get('flow_type') == 'basic_authenticated' else '&flow=enhanced'
            result_param = urllib.parse.quote(json.dumps(result))
            frontend_url = os.getenv('FRONTEND_URL', 'http://localhost:8001')
            redirect_url = f'{frontend_url}/?{provider}_auth=success{flow_param}&result={result_param}'
            self.send_response(302)
            self.send_header('Location', redirect_url)
            self.end_headers()
        else:
            self.send_error_redirect(result.get('error', f'{provider} auth failed'))

    def do_POST(self):
        print(f"POST request received: {self.path}")
        
        # Handle API authentication endpoint
        if self.path == '/api/authenticate':
            try:
                content_length = int(self.headers['Content-Length'])
                post_data = self.rfile.read(content_length)
                request_data = json.loads(post_data.decode('utf-8'))
                
                provider_type = request_data.get('provider_type')
                provider_token = request_data.get('provider_token')
                flow_type = request_data.get('flow_type', 'enhanced')
                
                print(f"DEBUG: Received provider_type: '{provider_type}'")
                print(f"DEBUG: Received flow_type: '{flow_type}'")
                
                if not provider_type or not provider_token:
                    self.send_response(400)
                    self.send_header('Content-Type', 'application/json')
                    self.add_cors_headers()
                    self.end_headers()
                    self.wfile.write(json.dumps({'success': False, 'error': 'Missing provider_type or provider_token'}).encode())
                    return
                
                # Execute the appropriate flow
                if flow_type == 'basic':
                    result = self.oauth_handler.handle_basic_flow(provider_type, provider_token)
                else:
                    print(f"DEBUG: Calling enhanced flow with provider_type: '{provider_type}'")
                    result = self.oauth_handler.handle_enhanced_flow(provider_type, provider_token)
                
                # For API calls, we don't have OAuth response data, so use placeholder
                if result.get('success') and provider_type not in ['Guest', 'Developer', 'UserPool']:
                    result['oauth_response'] = {'note': 'OAuth response not available for direct API calls'}
                    result['provider_name'] = provider_type
                
                # Add UserPool-specific OAuth response data for visualization
                if result.get('success') and provider_type == 'UserPool':
                    token_url = build_token_url(COGNITO_CONFIG["DOMAIN"], COGNITO_CONFIG["REGION"])
                    result['oauth_response'] = {'id_token': 'eyJraWQiOiJXWlMzZTNS...', 'token_type': 'Bearer', 'expires_in': 3600}
                    result['token_endpoint'] = token_url
                    result['provider_name'] = 'UserPool'
                
                self.send_response(200)
                self.send_header('Content-Type', 'application/json')
                self.add_cors_headers()
                self.end_headers()
                self.wfile.write(json.dumps(result).encode())
                return
                
            except Exception as e:
                self.send_response(500)
                self.send_header('Content-Type', 'application/json')
                self.add_cors_headers()
                self.end_headers()
                self.wfile.write(json.dumps({'success': False, 'error': str(e)}).encode())
                return
        
        # Handle SAML callback POST
        if self.path.startswith('/auth/saml/callback'):
            try:
                content_length = int(self.headers['Content-Length'])
                post_data = self.rfile.read(content_length)
                
                # Parse form data and extract SAML response
                form_data = urllib.parse.parse_qs(post_data.decode('utf-8'))
                saml_response = form_data.get('SAMLResponse', [None])[0]
                
                if not saml_response:
                    self.send_response(302)
                    self.send_header('Location', 'http://localhost:8001/?error=saml_no_response')
                    self.end_headers()
                    return
                
                # Process with Enhanced Flow using handler
                result = self.oauth_handler.handle_saml_flow(saml_response)
                
                # Store result and redirect
                if result.get('success'):
                    result_param = urllib.parse.quote(json.dumps(result))
                    flow_param = '&flow=enhanced&track_api_flow=true'  # SAML only works with enhanced flow
                    self.send_response(302)
                    self.send_header('Location', f'http://localhost:8001/?saml_auth=success{flow_param}&result={result_param}')
                    self.end_headers()
                else:
                    error_msg = urllib.parse.quote(result.get('error', 'SAML failed'))
                    self.send_response(302)
                    self.send_header('Location', f'http://localhost:8001/?error={error_msg}')
                    self.end_headers()
                    
            except Exception as e:
                error_msg = urllib.parse.quote(f"SAML error: {str(e)}")
                self.send_response(302)
                self.send_header('Location', f'http://localhost:8001/?error={error_msg}')
                self.end_headers()
            return
        
        # User Pool callback - handle OAuth code exchange with WAF token support
        if self.path == '/auth/userpool/callback':
            try:
                content_length = int(self.headers['Content-Length'])
                post_data = self.rfile.read(content_length)
                
                # Handle both JSON and form data
                content_type = self.headers.get('Content-Type', '')
                if 'application/json' in content_type:
                    request_data = json.loads(post_data.decode('utf-8'))
                else:
                    # Parse form data
                    from urllib.parse import parse_qs
                    form_data = parse_qs(post_data.decode('utf-8'))
                    request_data = {k: v[0] if v else '' for k, v in form_data.items()}
                
                auth_code = request_data.get('code')
                redirect_uri = request_data.get('redirect_uri')
                waf_token = request_data.get('waf_token')  # Optional WAF token
                
                if not auth_code:
                    self.send_error_response(400, 'Missing authorization code')
                    return
                
                # Try code exchange with WAF token support
                result = self.exchange_code_with_sdk(auth_code, redirect_uri, waf_token)
                
                # If this came from CAPTCHA form, redirect back to frontend
                if 'application/json' not in content_type:
                    if result.get('id_token'):
                        # Redirect to frontend with success
                        frontend_url = os.getenv('FRONTEND_URL', 'http://localhost:8001')
                        self.send_response(302)
                        self.send_header('Location', f'{frontend_url}/?userpool_auth=success&id_token={result["id_token"]}')
                        self.end_headers()
                    else:
                        # Redirect with error
                        error_msg = result.get('error', 'Token exchange failed')
                        frontend_url = os.getenv('FRONTEND_URL', 'http://localhost:8001')
                        self.send_response(302)
                        self.send_header('Location', f'{frontend_url}/?error={urllib.parse.quote(error_msg)}')
                        self.end_headers()
                else:
                    self.send_json_response(result)
                return
                
            except Exception as e:
                self.send_error_response(500, str(e))
            return
        
        # Direct User Pool authentication with WAF token support - COMMENTED OUT
        # if self.path == '/api/userpool-direct-auth':
        #     try:
        #         content_length = int(self.headers['Content-Length'])
        #         post_data = self.rfile.read(content_length)
        #         request_data = json.loads(post_data.decode('utf-8'))
        #         
        #         username = request_data.get('username')
        #         password = request_data.get('password')
        #         waf_token = request_data.get('waf_token')  # Optional WAF token
        #         
        #         if not username or not password:
        #             self.send_error_response(400, 'Missing username or password')
        #             return
        #         
        #         result = self.authenticate_user_pool_sdk(username, password, waf_token)
        #         self.send_json_response(result)
        #         return
        #         
        #     except Exception as e:
        #         self.send_error_response(500, str(e))
        #     return
        
        # Removed: CAPTCHA form handling - WAF handles natively
        
        # Handle other POST requests
        self.send_response(404)
        self.end_headers()
    
    def do_OPTIONS(self):
        self.send_response(200)
        self.add_cors_headers()
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
    
    def do_GET(self):
        # Serve frontend configuration
        if self.path == '/api/config.json':
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.add_cors_headers()
            self.end_headers()
            self.wfile.write(json.dumps(FRONTEND_CONFIG).encode())
            return

        # Generic OAuth handler for any provider
        if self.path.startswith('/auth/'):
            # Remove query parameters before parsing path
            clean_path = self.path.split('?')[0]
            path_parts = clean_path.split('/')
            
            if len(path_parts) >= 3:
                provider = path_parts[2]
                if len(path_parts) == 3: 
                    self.handle_oauth_login(provider)
                elif len(path_parts) == 4 and path_parts[3] == 'callback':
                    self.handle_oauth_callback(provider)
                else:
                    self.send_error_response(404, 'Not Found')
            else:
                self.send_error_response(404, 'Not Found')
        else:
            self.send_error_response(404, 'Not Found')
    
    def handle_oauth_login(self, provider):
        # Convert to lowercase for case-insensitive lookup
        provider_lower = provider.lower()
        if provider_lower not in OAUTH_PROVIDERS:
            self.send_error_response(404, f'Provider {provider} not supported')
            return
        
        # Special handling for SAML
        if provider_lower == 'saml':
            self.handle_saml_login()
            return
        
        # Use lowercase provider for config lookup
        provider = provider_lower
        
        # Check if basic flow is requested
        parsed_url = urlparse(self.path)
        query_params = parse_qs(parsed_url.query)
        flow_type = query_params.get('flow', ['enhanced'])[0]
        
        config = OAUTH_PROVIDERS[provider]
        redirect_uri = f"{os.getenv('APP_URL', 'http://localhost:8006')}/auth/{provider}/callback"
        
        # Standard OAuth 2.0 flow for other providers
        auth_url = (
            f"{config['auth_url']}?"
            f"client_id={config['client_id']}&"
            f"redirect_uri={urllib.parse.quote(redirect_uri)}&"
            f"response_type=code&"
            f"scope={urllib.parse.quote(config['scope'])}&"
            f"state={flow_type}"  # Pass flow type as state
        )
        
        self.send_response(302)
        self.send_header('Location', auth_url)
        self.end_headers()
    
    def handle_saml_login(self):
        """Handle SAML login initiation"""
        try:
            sso_url = os.getenv('SAML_SSO_URL', 'https://example.com/saml/sso')
            
            if 'example.com' in sso_url:
                self.send_error_redirect('SAML_SSO_URL not configured properly')
                return
            
            self.send_response(302)
            self.send_header('Location', sso_url)
            self.end_headers()
            
        except Exception as e:
            self.send_error_redirect(f'SAML login error: {str(e)}')
    
    def handle_oauth_callback(self, provider):
        try:
            # Convert to lowercase for case-insensitive lookup
            provider_lower = provider.lower()
            if provider_lower not in OAUTH_PROVIDERS:
                self.send_error_response(404, f'Provider {provider} not supported')
                return
            
            # Use lowercase provider for consistency
            provider = provider_lower
            
            # Parse request
            parsed_url = urlparse(self.path)
            query_params = parse_qs(parsed_url.query)
            code = query_params.get('code', [None])[0]
            state = query_params.get('state', [None])[0]
            
            if not code:
                return self.send_error_redirect(f'{provider}_auth_failed')
            
            # Use the handler
            redirect_uri = f"{os.getenv('APP_URL', 'http://localhost:8006')}/auth/{provider}/callback"
            token_result = self.oauth_handler.handle_token_exchange(provider, code, redirect_uri)
            
            if not token_result['success']:
                return self.send_error_redirect(token_result['error'])
            
            # Handle authentication flow
            if state == 'basic':
                result = self.oauth_handler.handle_basic_flow(provider, token_result['token'])
            else:
                result = self.oauth_handler.handle_enhanced_flow(provider, token_result['token'])
            
            # Add OAuth response data to result for visualization
            if result.get('success') and 'oauth_response' in token_result:
                result['oauth_response'] = token_result['oauth_response']
                result['token_endpoint'] = token_result['token_endpoint']
                result['provider_name'] = token_result['provider_name']
            
            print(f"DEBUG: Flow result: {result.get('success')}")
            
            # Send response
            self.send_success_redirect(provider, result)
            
        except Exception as e:
            self.send_error_redirect(f'OAuth callback error: {str(e)}')
    
    def send_json_response(self, data):
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.add_cors_headers()
        self.end_headers()
        self.wfile.write(json.dumps(data).encode())
    
    def exchange_code_with_sdk(self, auth_code, redirect_uri, waf_token=None):
        """Exchange OAuth code with WAF token support"""
        try:
            token_url = build_token_url(COGNITO_CONFIG["DOMAIN"], COGNITO_CONFIG["REGION"])
            
            token_data = {
                'grant_type': 'authorization_code',
                'client_id': COGNITO_CONFIG['APP_CLIENT_ID'],
                'client_secret': COGNITO_CONFIG['APP_CLIENT_SECRET'],
                'code': auth_code,
                'redirect_uri': redirect_uri
            }
            
            headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
                'User-Agent': 'AWS-SDK-Python/1.0'
            }
            
            # Add WAF token if provided
            if waf_token:
                headers['x-aws-waf-token'] = waf_token
            
            print(f"Making request to: {token_url}")
            print(f"Request data: {token_data}")
            
            response = requests.post(token_url, data=token_data, headers=headers, timeout=10)
            
            print(f"Response status: {response.status_code}")
            print(f"Response text: {response.text[:500]}")
            
            if response.status_code == 200:
                result = response.json()
                # Add OAuth visualization data for User Pool
                result['oauth_response'] = result.copy()
                result['token_endpoint'] = token_url
                result['provider_name'] = 'UserPool'
                return result
            else:
                return {
                    'error': 'TOKEN_EXCHANGE_FAILED',
                    'message': f'Token exchange failed with status {response.status_code}',
                    'status_code': response.status_code,
                    'response_text': response.text[:200]
                }
                
        except Exception as e:
            return {'error': str(e)}
            
    def send_error_response(self, status_code, message):
        self.send_response(status_code)
        self.send_header('Content-Type', 'application/json')
        self.add_cors_headers()
        self.end_headers()
        self.wfile.write(json.dumps({'error': message}).encode())

def run_server(port=8006):
    try:
        from backend.config.env_config import validate_required_env_vars
        validate_required_env_vars()
    except ImportError:
        try:
            from config.env_config import validate_required_env_vars
            validate_required_env_vars()
        except ImportError:
            pass
    except ValueError as e:
        print(f"Configuration Error: {e}")
        print("Please check your .env file and ensure all required variables are set.")
        return
    
    server_address = ('', port)
    httpd = HTTPServer(server_address, GenericOAuthHandler)
    print(f'Starting OAuth server on port {port}...')
    httpd.serve_forever()

if __name__ == '__main__':
    run_server()