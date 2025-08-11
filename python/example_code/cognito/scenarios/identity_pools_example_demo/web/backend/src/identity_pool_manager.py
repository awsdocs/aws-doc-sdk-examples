# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import logging
from botocore.exceptions import ClientError
from backend.config.cognito_config import COGNITO_CONFIG

logger = logging.getLogger(__name__)

class IdentityPoolManager:
    def __init__(self):
        self.cognito_identity = boto3.client(
            'cognito-identity',
            region_name=COGNITO_CONFIG['REGION'],
        )
        self.identity_pool_id = COGNITO_CONFIG['IDENTITY_POOL_ID']
        
        # Get role ARNs from identity pool configuration
        try:
            response = self.cognito_identity.get_identity_pool_roles(
                IdentityPoolId=self.identity_pool_id
            )
            self.authenticated_role_arn = response.get('Roles', {}).get('authenticated')
            self.unauthenticated_role_arn = response.get('Roles', {}).get('unauthenticated')
            
            if not self.authenticated_role_arn or not self.unauthenticated_role_arn:
                logger.warning("Role ARNs not found in identity pool configuration")
                # Use default role ARNs from config if available
                self.authenticated_role_arn = COGNITO_CONFIG.get('AUTHENTICATED_ROLE_ARN')
                self.unauthenticated_role_arn = COGNITO_CONFIG.get('UNAUTHENTICATED_ROLE_ARN')
        except Exception as e:
            logger.warning(f"Could not get identity pool roles: {str(e)}")
            # Use default role ARNs from config if available
            self.authenticated_role_arn = COGNITO_CONFIG.get('AUTHENTICATED_ROLE_ARN')
            self.unauthenticated_role_arn = COGNITO_CONFIG.get('UNAUTHENTICATED_ROLE_ARN')

    def get_id(self, logins=None):
        try:
            params = {'IdentityPoolId': self.identity_pool_id}
            if logins is not None:
                params['Logins'] = logins
            response = self.cognito_identity.get_id(**params)
            return response['IdentityId']
        except ClientError as error:
            logger.error("Failed to get identity ID: %s", error)
            raise

    def get_credentials_for_identity(self, identity_id, logins=None):
        try:
            params = {'IdentityId': identity_id}
            if logins is not None:
                params['Logins'] = logins
            response = self.cognito_identity.get_credentials_for_identity(**params)
            return response['Credentials']
        except ClientError as error:
            logger.error("Failed to get credentials: %s", error)
            raise

    def get_open_id_token(self, identity_id, logins=None):
        try:
            params = {
                'IdentityId': identity_id
            }
            if logins:
                params['Logins'] = logins
                
            response = self.cognito_identity.get_open_id_token(**params)
            return response['Token']
        except ClientError as error:
            logger.error("Failed to get OpenID token: %s", error)
            raise
    
    def get_open_id_token_for_developer_identity(self, logins, token_duration=3600):
        try:
            response = self.cognito_identity.get_open_id_token_for_developer_identity(
                IdentityPoolId=self.identity_pool_id,
                Logins=logins,
                TokenDuration=token_duration
            )
            return response['IdentityId'], response['Token']
        except ClientError as error:
            logger.error("Failed to get developer token: %s", error)
            raise
    
    def assume_role_with_web_identity(self, role_arn, role_session_name, web_identity_token, duration_seconds=3600):
        try:
            sts = boto3.client('sts', region_name=COGNITO_CONFIG['REGION'])
            response = sts.assume_role_with_web_identity(
                RoleArn=role_arn,
                RoleSessionName=role_session_name,
                WebIdentityToken=web_identity_token,
                DurationSeconds=duration_seconds
            )
            return response['Credentials']
        except ClientError as error:
            logger.error("Failed to assume role: %s", error)
            raise
