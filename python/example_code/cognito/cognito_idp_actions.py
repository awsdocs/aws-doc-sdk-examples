# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Cognito to
sign up a user, register a multi-factor authentication (MFA) application, sign in
using an MFA code, and sign in using a tracked device.
"""

import base64
import hashlib
import hmac
import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cognito-idp.helper.CognitoIdentityProviderWrapper.decl]
class CognitoIdentityProviderWrapper:
    """Encapsulates Amazon Cognito actions"""
    def __init__(self, cognito_idp_client, user_pool_id, client_id, client_secret=None):
        """
        :param cognito_idp_client: A Boto3 Amazon Cognito Identity Provider client.
        :param user_pool_id: The ID of an existing Amazon Cognito user pool.
        :param client_id: The ID of a client application registered with the user pool.
        :param client_secret: The client secret, if the client has a secret.
        """
        self.cognito_idp_client = cognito_idp_client
        self.user_pool_id = user_pool_id
        self.client_id = client_id
        self.client_secret = client_secret
# snippet-end:[python.example_code.cognito-idp.helper.CognitoIdentityProviderWrapper.decl]

    def _secret_hash(self, user_name):
        """
        Calculates a secret hash from a user name and a client secret.

        :param user_name: The user name to use when calculating the hash.
        :return: The secret hash.
        """
        key = self.client_secret.encode()
        msg = bytes(user_name + self.client_id, 'utf-8')
        secret_hash = base64.b64encode(
            hmac.new(key, msg, digestmod=hashlib.sha256).digest()).decode()
        logger.info("Made secret hash for %s: %s.", user_name, secret_hash)
        return secret_hash

    # snippet-start:[python.example_code.cognito-idp.SignUp]
    def sign_up_user(self, user_name, password, user_email):
        """
        Signs up a new user with Amazon Cognito. This action prompts Amazon Cognito
        to send an email to the specified email address. The email contains a code that
        can be used to confirm the user.

        When the user already exists, the user status is checked to determine whether
        the user has been confirmed.

        :param user_name: The user name that identifies the new user.
        :param password: The password for the new user.
        :param user_email: The email address for the new user.
        :return: True when the user is already confirmed with Amazon Cognito.
                 Otherwise, false.
        """
        try:
            kwargs = {
                'ClientId': self.client_id, 'Username': user_name, 'Password': password,
                'UserAttributes': [{'Name': 'email', 'Value': user_email}]}
            if self.client_secret is not None:
                kwargs['SecretHash'] = self._secret_hash(user_name)
            response = self.cognito_idp_client.sign_up(**kwargs)
            confirmed = response['UserConfirmed']
        except ClientError as err:
            if err.response['Error']['Code'] == 'UsernameExistsException':
                response = self.cognito_idp_client.admin_get_user(
                    UserPoolId=self.user_pool_id, Username=user_name)
                logger.warning("User %s exists and is %s.", user_name, response['UserStatus'])
                confirmed = response['UserStatus'] == 'CONFIRMED'
            else:
                logger.error(
                    "Couldn't sign up %s. Here's why: %s: %s", user_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        return confirmed
    # snippet-end:[python.example_code.cognito-idp.SignUp]

    # snippet-start:[python.example_code.cognito-idp.ResendConfirmationCode]
    def resend_confirmation(self, user_name):
        """
        Prompts Amazon Cognito to resend an email with a new confirmation code.

        :param user_name: The name of the user who will receive the email.
        :return: Delivery information about where the email is sent.
        """
        try:
            kwargs = {
                'ClientId': self.client_id, 'Username': user_name}
            if self.client_secret is not None:
                kwargs['SecretHash'] = self._secret_hash(user_name)
            response = self.cognito_idp_client.resend_confirmation_code(**kwargs)
            delivery = response['CodeDeliveryDetails']
        except ClientError as err:
            logger.error(
                "Couldn't resend confirmation to %s. Here's why: %s: %s", user_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return delivery
    # snippet-end:[python.example_code.cognito-idp.ResendConfirmationCode]

    # snippet-start:[python.example_code.cognito-idp.ConfirmSignUp]
    def confirm_user_sign_up(self, user_name, confirmation_code):
        """
        Confirms a previously created user. A user must be confirmed before they
        can sign in to Amazon Cognito.

        :param user_name: The name of the user to confirm.
        :param confirmation_code: The confirmation code sent to the user's registered
                                  email address.
        :return: True when the confirmation succeeds.
        """
        try:
            kwargs = {
                'ClientId': self.client_id, 'Username': user_name,
                'ConfirmationCode': confirmation_code}
            if self.client_secret is not None:
                kwargs['SecretHash'] = self._secret_hash(user_name)
            self.cognito_idp_client.confirm_sign_up(**kwargs)
        except ClientError as err:
            logger.error(
                "Couldn't confirm sign up for %s. Here's why: %s: %s", user_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return True
    # snippet-end:[python.example_code.cognito-idp.ConfirmSignUp]

    # snippet-start:[python.example_code.cognito-idp.ListUsers]
    def list_users(self):
        """
        Returns a list of the users in the current user pool.

        :return: The list of users.
        """
        try:
            response = self.cognito_idp_client.list_users(UserPoolId=self.user_pool_id)
            users = response['Users']
        except ClientError as err:
            logger.error(
                "Couldn't list users for %s. Here's why: %s: %s", self.user_pool_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return users
    # snippet-end:[python.example_code.cognito-idp.ListUsers]

    # snippet-start:[python.example_code.cognito-idp.AdminInitiateAuth]
    def start_sign_in(self, user_name, password):
        """
        Starts the sign-in process for a user by using administrator credentials.
        This method of signing in is appropriate for code running on a secure server.

        If the user pool is configured to require MFA and this is the first sign-in
        for the user, Amazon Cognito returns a challenge response to set up an
        MFA application. When this occurs, this function gets an MFA secret from
        Amazon Cognito and returns it to the caller.

        :param user_name: The name of the user to sign in.
        :param password: The user's password.
        :return: The result of the sign-in attempt. When sign-in is successful, this
                 returns an access token that can be used to get AWS credentials. Otherwise,
                 Amazon Cognito returns a challenge to set up an MFA application,
                 or a challenge to enter an MFA code from a registered MFA application.
        """
        try:
            kwargs = {
                'UserPoolId': self.user_pool_id,
                'ClientId': self.client_id,
                'AuthFlow': 'ADMIN_USER_PASSWORD_AUTH',
                'AuthParameters': {'USERNAME': user_name, 'PASSWORD': password}}
            if self.client_secret is not None:
                kwargs['AuthParameters']['SECRET_HASH'] = self._secret_hash(user_name)
            response = self.cognito_idp_client.admin_initiate_auth(**kwargs)
            challenge_name = response.get('ChallengeName', None)
            if challenge_name == 'MFA_SETUP':
                if 'SOFTWARE_TOKEN_MFA' in response['ChallengeParameters']['MFAS_CAN_SETUP']:
                    response.update(self.get_mfa_secret(response['Session']))
                else:
                    raise RuntimeError(
                        "The user pool requires MFA setup, but the user pool is not "
                        "configured for TOTP MFA. This example requires TOTP MFA.")
        except ClientError as err:
            logger.error(
                "Couldn't start sign in for %s. Here's why: %s: %s", user_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            response.pop('ResponseMetadata', None)
            return response
    # snippet-end:[python.example_code.cognito-idp.AdminInitiateAuth]

    # snippet-start:[python.example_code.cognito-idp.AssociateSoftwareToken]
    def get_mfa_secret(self, session):
        """
        Gets a token that can be used to associate an MFA application with the user.

        :param session: Session information returned from a previous call to initiate
                        authentication.
        :return: An MFA token that can be used to set up an MFA application.
        """
        try:
            response = self.cognito_idp_client.associate_software_token(Session=session)
        except ClientError as err:
            logger.error(
                "Couldn't get MFA secret. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            response.pop('ResponseMetadata', None)
            return response
    # snippet-end:[python.example_code.cognito-idp.AssociateSoftwareToken]

    # snippet-start:[python.example_code.cognito-idp.VerifySoftwareToken]
    def verify_mfa(self, session, user_code):
        """
        Verify a new MFA application that is associated with a user.

        :param session: Session information returned from a previous call to initiate
                        authentication.
        :param user_code: A code generated by the associated MFA application.
        :return: Status that indicates whether the MFA application is verified.
        """
        try:
            response = self.cognito_idp_client.verify_software_token(
                Session=session, UserCode=user_code)
        except ClientError as err:
            logger.error(
                "Couldn't verify MFA. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            response.pop('ResponseMetadata', None)
            return response
    # snippet-end:[python.example_code.cognito-idp.VerifySoftwareToken]

    # snippet-start:[python.example_code.cognito-idp.AdminRespondToAuthChallenge]
    def respond_to_mfa_challenge(self, user_name, session, mfa_code):
        """
        Responds to a challenge for an MFA code. This completes the second step of
        a two-factor sign-in. When sign-in is successful, it returns an access token
        that can be used to get AWS credentials from Amazon Cognito.

        :param user_name: The name of the user who is signing in.
        :param session: Session information returned from a previous call to initiate
                        authentication.
        :param mfa_code: A code generated by the associated MFA application.
        :return: The result of the authentication. When successful, this contains an
                 access token for the user.
        """
        try:
            kwargs = {
                'UserPoolId': self.user_pool_id,
                'ClientId': self.client_id,
                'ChallengeName': 'SOFTWARE_TOKEN_MFA', 'Session': session,
                'ChallengeResponses': {
                    'USERNAME': user_name, 'SOFTWARE_TOKEN_MFA_CODE': mfa_code}}
            if self.client_secret is not None:
                kwargs['ChallengeResponses']['SECRET_HASH'] = self._secret_hash(user_name)
            response = self.cognito_idp_client.admin_respond_to_auth_challenge(**kwargs)
            auth_result = response['AuthenticationResult']
        except ClientError as err:
            if err.response['Error']['Code'] == 'ExpiredCodeException':
                logger.warning(
                    "Your MFA code has expired or has been used already. You might have "
                    "to wait a few seconds until your app shows you a new code.")
            else:
                logger.error(
                    "Couldn't respond to mfa challenge for %s. Here's why: %s: %s", user_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return auth_result
    # snippet-end:[python.example_code.cognito-idp.AdminRespondToAuthChallenge]

    # snippet-start:[python.example_code.cognito-idp.ConfirmDevice]
    def confirm_mfa_device(
            self, user_name, device_key, device_group_key, device_password, access_token,
            aws_srp):
        """
        Confirms an MFA device to be tracked by Amazon Cognito. When a device is
        tracked, its key and password can be used to sign in without requiring a new
        MFA code from the MFA application.

        :param user_name: The user that is associated with the device.
        :param device_key: The key of the device, returned by Amazon Cognito.
        :param device_group_key: The group key of the device, returned by Amazon Cognito.
        :param device_password: The password that is associated with the device.
        :param access_token: The user's access token.
        :param aws_srp: A class that helps with Secure Remote Password (SRP)
                        calculations. The scenario associated with this example uses
                        the warrant package.
        :return: True when the user must confirm the device. Otherwise, False. When
                 False, the device is automatically confirmed and tracked.
        """
        srp_helper = aws_srp.AWSSRP(
            username=user_name, password=device_password,
            pool_id='_', client_id=self.client_id, client_secret=None,
            client=self.cognito_idp_client)
        device_and_pw = f'{device_group_key}{device_key}:{device_password}'
        device_and_pw_hash = aws_srp.hash_sha256(device_and_pw.encode('utf-8'))
        salt = aws_srp.pad_hex(aws_srp.get_random(16))
        x_value = aws_srp.hex_to_long(aws_srp.hex_hash(salt + device_and_pw_hash))
        verifier = aws_srp.pad_hex(pow(srp_helper.g, x_value, srp_helper.big_n))
        device_secret_verifier_config = {
            "PasswordVerifier": base64.standard_b64encode(bytearray.fromhex(verifier)).decode('utf-8'),
            "Salt": base64.standard_b64encode(bytearray.fromhex(salt)).decode('utf-8')
        }
        try:
            response = self.cognito_idp_client.confirm_device(
                AccessToken=access_token,
                DeviceKey=device_key,
                DeviceSecretVerifierConfig=device_secret_verifier_config)
            user_confirm = response['UserConfirmationNecessary']
        except ClientError as err:
            logger.error(
                "Couldn't confirm mfa device %s. Here's why: %s: %s", device_key,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return user_confirm
    # snippet-end:[python.example_code.cognito-idp.ConfirmDevice]

    # snippet-start:[python.example_code.cognito-idp.InitiateAuth]
    # snippet-start:[python.example_code.cognito-idp.RespondToAuthChallenge]
    def sign_in_with_tracked_device(
            self, user_name, password, device_key, device_group_key, device_password,
            aws_srp):
        """
        Signs in to Amazon Cognito as a user who has a tracked device. Signing in
        with a tracked device lets a user sign in without entering a new MFA code.

        Signing in with a tracked device requires that the client respond to the SRP
        protocol. The scenario associated with this example uses the warrant package
        to help with SRP calculations.

        For more information on SRP, see https://en.wikipedia.org/wiki/Secure_Remote_Password_protocol.

        :param user_name: The user that is associated with the device.
        :param password: The user's password.
        :param device_key: The key of a tracked device.
        :param device_group_key: The group key of a tracked device.
        :param device_password: The password that is associated with the device.
        :param aws_srp: A class that helps with SRP calculations. The scenario
                        associated with this example uses the warrant package.
        :return: The result of the authentication. When successful, this contains an
                 access token for the user.
        """
        try:
            srp_helper = aws_srp.AWSSRP(
                username=user_name, password=device_password,
                pool_id='_', client_id=self.client_id, client_secret=None,
                client=self.cognito_idp_client)

            response_init = self.cognito_idp_client.initiate_auth(
                ClientId=self.client_id, AuthFlow='USER_PASSWORD_AUTH',
                AuthParameters={
                    'USERNAME': user_name, 'PASSWORD': password, 'DEVICE_KEY': device_key})
            if response_init['ChallengeName'] != 'DEVICE_SRP_AUTH':
                raise RuntimeError(
                    f"Expected DEVICE_SRP_AUTH challenge but got {response_init['ChallengeName']}.")

            auth_params = srp_helper.get_auth_params()
            auth_params['DEVICE_KEY'] = device_key
            response_auth = self.cognito_idp_client.respond_to_auth_challenge(
                ClientId=self.client_id,
                ChallengeName='DEVICE_SRP_AUTH',
                ChallengeResponses=auth_params
            )
            if response_auth['ChallengeName'] != 'DEVICE_PASSWORD_VERIFIER':
                raise RuntimeError(
                    f"Expected DEVICE_PASSWORD_VERIFIER challenge but got "
                    f"{response_init['ChallengeName']}.")

            challenge_params = response_auth['ChallengeParameters']
            challenge_params['USER_ID_FOR_SRP'] = device_group_key + device_key
            cr = srp_helper.process_challenge(challenge_params)
            cr['USERNAME'] = user_name
            cr['DEVICE_KEY'] = device_key
            response_verifier = self.cognito_idp_client.respond_to_auth_challenge(
                ClientId=self.client_id,
                ChallengeName='DEVICE_PASSWORD_VERIFIER',
                ChallengeResponses=cr)
            auth_tokens = response_verifier['AuthenticationResult']
        except ClientError as err:
            logger.error(
                "Couldn't start client sign in for %s. Here's why: %s: %s", user_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return auth_tokens
    # snippet-end:[python.example_code.cognito-idp.RespondToAuthChallenge]
    # snippet-end:[python.example_code.cognito-idp.InitiateAuth]
