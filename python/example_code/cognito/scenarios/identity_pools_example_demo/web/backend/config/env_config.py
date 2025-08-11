
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
from dotenv import load_dotenv

# Load .env file from web directory (three levels up from backend/config/)
web_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
load_dotenv(os.path.join(web_dir, '.env'), override=True)

# Validate required variables
required_vars = ['COGNITO_IDENTITY_POOL_ID', 'AWS_REGION', 'AWS_ACCOUNT_ID']
missing = [var for var in required_vars if not os.getenv(var)]
if missing:
    raise ValueError(f"Missing required environment variables: {', '.join(missing)}")

def validate_required_env_vars():
    """Validation function for oauth_server.py startup check"""
    if missing:
        raise ValueError(f"Missing required environment variables: {', '.join(missing)}")

FRONTEND_CONFIG = {
    'region': os.getenv('AWS_REGION'),
    'userPoolId': os.getenv('COGNITO_USER_POOL_ID'),
    'clientId': os.getenv('COGNITO_APP_CLIENT_ID'),
    'userPoolDomain': os.getenv('COGNITO_DOMAIN'),
    'apiEndpoint': f"{os.getenv('APP_URL', 'http://localhost:8006')}/api/authenticate"
}
