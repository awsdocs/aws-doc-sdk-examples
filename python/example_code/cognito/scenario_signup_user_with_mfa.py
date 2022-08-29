# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows you how to use the AWS SDK for Python (Boto3) with Amazon Cognito to
do the following:

1. Sign up a user with a user name, password, and email address.
2. Confirm the user from a code sent in email.
3. Set up multi-factor authentication by associating an MFA application with the user.
4. Sign in by using a password and an MFA code.
5. Register an MFA device to be tracked by Amazon Cognito.
6. Sign in by using a password and information from the tracked device. This avoids the
   need to enter a new MFA code.

This scenario requires the following resources:

* An existing Amazon Cognito user pool that is configured to allow self sign-up.
* A client ID to use for authenticating with Amazon Cognito.
"""

import argparse
import base64
import logging
import os
from pprint import pp
import sys
import webbrowser

import boto3
import qrcode
from warrant import aws_srp

from cognito_idp_actions import CognitoIdentityProviderWrapper

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append('../..')
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cognito-idp.Scenario_SignUpUserWithMfa]
def run_scenario(cognito_idp_client, user_pool_id, client_id):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the Amazon Cognito user signup with MFA demo.")
    print('-'*88)

    cog_wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, user_pool_id, client_id)

    user_name = q.ask("Let's sign up a new user. Enter a user name: ", q.non_empty)
    password = q.ask("Enter a password for the user: ", q.non_empty)
    email = q.ask("Enter a valid email address that you own: ", q.non_empty)
    confirmed = cog_wrapper.sign_up_user(user_name, password, email)
    while not confirmed:
        print(f"User {user_name} requires confirmation. Check {email} for "
              f"a verification code.")
        confirmation_code = q.ask("Enter the confirmation code from the email: ")
        if not confirmation_code:
            if q.ask("Do you need another confirmation code (y/n)? ", q.is_yesno):
                delivery = cog_wrapper.resend_confirmation(user_name)
                print(f"Confirmation code sent by {delivery['DeliveryMedium']} "
                      f"to {delivery['Destination']}.")
        else:
            confirmed = cog_wrapper.confirm_user_sign_up(user_name, confirmation_code)
    print(f"User {user_name} is confirmed and ready to use.")
    print('-'*88)

    print("Let's get a list of users in the user pool.")
    q.ask("Press Enter when you're ready.")
    users = cog_wrapper.list_users()
    if users:
        print(f"Found {len(users)} users:")
        pp(users)
    else:
        print("No users found.")
    print('-'*88)

    print("Let's sign in and get an access token.")
    auth_tokens = None
    challenge = 'ADMIN_USER_PASSWORD_AUTH'
    response = {}
    while challenge is not None:
        if challenge == 'ADMIN_USER_PASSWORD_AUTH':
            response = cog_wrapper.start_sign_in(user_name, password)
            challenge = response['ChallengeName']
        elif response['ChallengeName'] == 'MFA_SETUP':
            print("First, we need to set up an MFA application.")
            qr_img = qrcode.make(
                f"otpauth://totp/{user_name}?secret={response['SecretCode']}")
            qr_img.save("qr.png")
            q.ask("Press Enter to see a QR code on your screen. Scan it into an MFA "
                  "application, such as Google Authenticator.")
            webbrowser.open("qr.png")
            mfa_code = q.ask(
                "Enter the verification code from your MFA application: ", q.non_empty)
            response = cog_wrapper.verify_mfa(response['Session'], mfa_code)
            print(f"MFA device setup {response['Status']}")
            print("Now that an MFA application is set up, let's sign in again.")
            print("You might have to wait a few seconds for a new MFA code to appear in "
                  "your MFA application.")
            challenge = 'ADMIN_USER_PASSWORD_AUTH'
        elif response['ChallengeName'] == 'SOFTWARE_TOKEN_MFA':
            auth_tokens = None
            while auth_tokens is None:
                mfa_code = q.ask(
                    "Enter a verification code from your MFA application: ", q.non_empty)
                auth_tokens = cog_wrapper.respond_to_mfa_challenge(
                    user_name, response['Session'], mfa_code)
            print(f"You're signed in as {user_name}.")
            print("Here's your access token:")
            pp(auth_tokens['AccessToken'])
            print("And your device information:")
            pp(auth_tokens['NewDeviceMetadata'])
            challenge = None
        else:
            raise Exception(f"Got unexpected challenge {response['ChallengeName']}")
    print('-'*88)

    device_group_key = auth_tokens['NewDeviceMetadata']['DeviceGroupKey']
    device_key = auth_tokens['NewDeviceMetadata']['DeviceKey']
    device_password = base64.standard_b64encode(os.urandom(40)).decode('utf-8')

    print("Let's confirm your MFA device so you don't have re-enter MFA tokens for it.")
    q.ask("Press Enter when you're ready.")
    cog_wrapper.confirm_mfa_device(
        user_name, device_key, device_group_key, device_password,
        auth_tokens['AccessToken'], aws_srp)
    print(f"Your device {device_key} is confirmed.")
    print('-'*88)

    print(f"Now let's sign in as {user_name} from your confirmed device {device_key}.\n"
          f"Because this device is tracked by Amazon Cognito, you won't have to re-enter an MFA code.")
    q.ask("Press Enter when ready.")
    auth_tokens = cog_wrapper.sign_in_with_tracked_device(
        user_name, password, device_key, device_group_key, device_password, aws_srp)
    print("You're signed in. Your access token is:")
    pp(auth_tokens['AccessToken'])
    print('-'*88)

    print("Don't forget to delete your user pool when you're done with this example.")
    print("\nThanks for watching!")
    print('-'*88)


def main():
    parser = argparse.ArgumentParser(
        description="Shows how to sign up a new user with Amazon Cognito and associate "
                    "the user with an MFA application for multi-factor authentication.")
    parser.add_argument('user_pool_id', help="The ID of the user pool to use for the example.")
    parser.add_argument('client_id', help="The ID of the client application to use for the example.")
    args = parser.parse_args()
    try:
        run_scenario(boto3.client('cognito-idp'), args.user_pool_id, args.client_id)
    except Exception:
        logging.exception("Something went wrong with the demo.")


if __name__ == '__main__':
    main()
# snippet-end:[python.example_code.cognito-idp.Scenario_SignUpUserWithMfa]
