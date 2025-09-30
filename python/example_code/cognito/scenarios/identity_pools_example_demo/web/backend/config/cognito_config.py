# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
from dotenv import load_dotenv

# Load .env file from web directory
web_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
load_dotenv(os.path.join(web_dir, '.env'), override=True)

COGNITO_CONFIG = {
    'REGION': os.getenv('AWS_REGION'),
    'USER_POOL_ID': os.getenv('COGNITO_USER_POOL_ID'),
    'IDENTITY_POOL_ID': os.getenv('COGNITO_IDENTITY_POOL_ID'),
    'APP_CLIENT_ID': os.getenv('COGNITO_APP_CLIENT_ID'),
    'APP_CLIENT_SECRET': os.getenv('COGNITO_APP_CLIENT_SECRET'),
    'DOMAIN': os.getenv('COGNITO_DOMAIN'),
    'ACCOUNT_ID': os.getenv('AWS_ACCOUNT_ID'),
    'SAML_PROVIDER': os.getenv('SAML_PROVIDER'),
    'OIDC_PROVIDER_URL': os.getenv('OIDC_PROVIDER_URL'),
    'DEVELOPER_PROVIDER_NAME': os.getenv('DEVELOPER_PROVIDER_NAME'),
    'AUTHENTICATED_ROLE_ARN': os.getenv('AUTHENTICATED_ROLE_ARN'),
    'UNAUTHENTICATED_ROLE_ARN': os.getenv('UNAUTHENTICATED_ROLE_ARN')
}

# Validate required configuration
try:
    from backend.config.env_config import validate_required_env_vars
    validate_required_env_vars()
except ImportError:
    pass