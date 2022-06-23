# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to assume a role that requires a multi-factor authentication (MFA) token,
using AWS Security Token Service (STS) credentials.
"""

import json
import os
import time
import sys
import webbrowser
import boto3
from botocore.exceptions import ClientError


def progress_bar(seconds):
    """Shows a simple progress bar in the command window."""
    for _ in range(seconds):
        time.sleep(1)
        print('.', end='')
        sys.stdout.flush()
    print()


def unique_name(base_name):
    return f'demo-assume-role-{base_name}-{time.time_ns()}'


# snippet-start:[python.example_code.sts.Scenario_AssumeRoleMfa_setup]
def setup(iam_resource):
    """
    Creates a new user with no permissions.
    Creates a new virtual MFA device.
    Displays the QR code to seed the device.
    Asks for two codes from the MFA device.
    Registers the MFA device for the user.
    Creates an access key pair for the user.
    Creates a role with a policy that lets the user assume the role and requires MFA.
    Creates a policy that allows listing Amazon S3 buckets.
    Attaches the policy to the role.
    Creates an inline policy for the user that lets the user assume the role.

    For demonstration purposes, the user is created in the same account as the role,
    but in practice the user would likely be from another account.

    Any MFA device that can scan a QR code will work with this demonstration.
    Common choices are mobile apps like LastPass Authenticator,
    Microsoft Authenticator, or Google Authenticator.

    :param iam_resource: A Boto3 AWS Identity and Access Management (IAM) resource
                         that has permissions to create users, roles, and policies
                         in the account.
    :return: The newly created user, user key, virtual MFA device, and role.
    """
    user = iam_resource.create_user(UserName=unique_name('user'))
    print(f"Created user {user.name}.")

    virtual_mfa_device = iam_resource.create_virtual_mfa_device(
        VirtualMFADeviceName=unique_name('mfa'))
    print(f"Created virtual MFA device {virtual_mfa_device.serial_number}")

    print(f"Showing the QR code for the device. Scan this in the MFA app of your "
          f"choice.")
    with open('qr.png', 'wb') as qr_file:
        qr_file.write(virtual_mfa_device.qr_code_png)
    webbrowser.open(qr_file.name)

    print(f"Enter two consecutive code from your MFA device.")
    mfa_code_1 = input("Enter the first code: ")
    mfa_code_2 = input("Enter the second code: ")
    user.enable_mfa(
        SerialNumber=virtual_mfa_device.serial_number,
        AuthenticationCode1=mfa_code_1,
        AuthenticationCode2=mfa_code_2)
    os.remove(qr_file.name)
    print(f"MFA device is registered with the user.")

    user_key = user.create_access_key_pair()
    print(f"Created access key pair for user.")

    print(f"Wait for user to be ready.", end='')
    progress_bar(10)

    role = iam_resource.create_role(
        RoleName=unique_name('role'),
        AssumeRolePolicyDocument=json.dumps({
            'Version': '2012-10-17',
            'Statement': [
                {
                    'Effect': 'Allow',
                    'Principal': {'AWS': user.arn},
                    'Action': 'sts:AssumeRole',
                    'Condition': {'Bool': {'aws:MultiFactorAuthPresent': True}}
                }
            ]
        })
    )
    print(f"Created role {role.name} that requires MFA.")

    policy = iam_resource.create_policy(
        PolicyName=unique_name('policy'),
        PolicyDocument=json.dumps({
            'Version': '2012-10-17',
            'Statement': [
                {
                    'Effect': 'Allow',
                    'Action': 's3:ListAllMyBuckets',
                    'Resource': 'arn:aws:s3:::*'
                }
            ]
        })
    )
    role.attach_policy(PolicyArn=policy.arn)
    print(f"Created policy {policy.policy_name} and attached it to the role.")

    user.create_policy(
        PolicyName=unique_name('user-policy'),
        PolicyDocument=json.dumps({
            'Version': '2012-10-17',
            'Statement': [
                {
                    'Effect': 'Allow',
                    'Action': 'sts:AssumeRole',
                    'Resource': role.arn
                }
            ]
        })
    )
    print(f"Created an inline policy for {user.name} that lets the user assume "
          f"the role.")

    print("Give AWS time to propagate these new resources and connections.", end='')
    progress_bar(10)

    return user, user_key, virtual_mfa_device, role
# snippet-end:[python.example_code.sts.Scenario_AssumeRoleMfa_setup]


# snippet-start:[python.example_code.sts.Scenario_AssumeRoleMfa_access_denied]
def try_to_assume_role_without_mfa(assume_role_arn, session_name, sts_client):
    """
    Shows that attempting to assume the role without sending MFA credentials results
    in an AccessDenied error.

    :param assume_role_arn: The Amazon Resource Name (ARN) of the role to assume.
    :param session_name: The name of the STS session.
    :param sts_client: A Boto3 STS instance that has permission to assume the role.
    """
    print(f"Trying to assume the role without sending MFA credentials...")
    try:
        sts_client.assume_role(RoleArn=assume_role_arn, RoleSessionName=session_name)
        raise RuntimeError("Expected AccessDenied error.")
    except ClientError as error:
        if error.response['Error']['Code'] == 'AccessDenied':
            print("Got AccessDenied.")
        else:
            raise
# snippet-end:[python.example_code.sts.Scenario_AssumeRoleMfa_access_denied]


# snippet-start:[python.example_code.sts.Scenario_AssumeRoleMfa_list_buckets]
def list_buckets_from_assumed_role_with_mfa(
        assume_role_arn, session_name, mfa_serial_number, mfa_totp, sts_client):
    """
    Assumes a role from another account and uses the temporary credentials from
    that role to list the Amazon S3 buckets that are owned by the other account.
    Requires an MFA device serial number and token.

    The assumed role must grant permission to list the buckets in the other account.

    :param assume_role_arn: The Amazon Resource Name (ARN) of the role that
                            grants access to list the other account's buckets.
    :param session_name: The name of the STS session.
    :param mfa_serial_number: The serial number of the MFA device. For a virtual MFA
                              device, this is an ARN.
    :param mfa_totp: A time-based, one-time password issued by the MFA device.
    :param sts_client: A Boto3 STS instance that has permission to assume the role.
    """
    response = sts_client.assume_role(
        RoleArn=assume_role_arn,
        RoleSessionName=session_name,
        SerialNumber=mfa_serial_number,
        TokenCode=mfa_totp)
    temp_credentials = response['Credentials']
    print(f"Assumed role {assume_role_arn} and got temporary credentials.")

    s3_resource = boto3.resource(
        's3',
        aws_access_key_id=temp_credentials['AccessKeyId'],
        aws_secret_access_key=temp_credentials['SecretAccessKey'],
        aws_session_token=temp_credentials['SessionToken'])

    print(f"Listing buckets for the assumed role's account:")
    for bucket in s3_resource.buckets.all():
        print(bucket.name)
# snippet-end:[python.example_code.sts.Scenario_AssumeRoleMfa_list_buckets]


# snippet-start:[python.example_code.sts.Scenario_AssumeRoleMfa_teardown]
def teardown(user, virtual_mfa_device, role):
    """
    Removes all resources created during setup.

    :param user: The demo user.
    :param role: The demo role.
    """
    for attached in role.attached_policies.all():
        policy_name = attached.policy_name
        role.detach_policy(PolicyArn=attached.arn)
        attached.delete()
        print(f"Detached and deleted {policy_name}.")
    role.delete()
    print(f"Deleted {role.name}.")
    for user_pol in user.policies.all():
        user_pol.delete()
        print("Deleted inline user policy.")
    for key in user.access_keys.all():
        key.delete()
        print("Deleted user's access key.")
    for mfa in user.mfa_devices.all():
        mfa.disassociate()
    virtual_mfa_device.delete()
    user.delete()
    print(f"Deleted {user.name}.")
# snippet-end:[python.example_code.sts.Scenario_AssumeRoleMfa_teardown]


# snippet-start:[python.example_code.sts.Scenario_AssumeRoleMfa_demo]
def usage_demo():
    """Drives the demonstration."""
    print('-'*88)
    print(f"Welcome to the AWS Security Token Service assume role demo, "
          f"starring multi-factor authentication (MFA)!")
    print('-'*88)
    iam_resource = boto3.resource('iam')
    user, user_key, virtual_mfa_device, role = setup(iam_resource)
    print(f"Created {user.name} and {role.name}.")
    try:
        sts_client = boto3.client(
            'sts', aws_access_key_id=user_key.id, aws_secret_access_key=user_key.secret)
        try_to_assume_role_without_mfa(role.arn, 'demo-sts-session', sts_client)
        mfa_totp = input('Enter the code from your registered MFA device: ')
        list_buckets_from_assumed_role_with_mfa(
            role.arn, 'demo-sts-session', virtual_mfa_device.serial_number,
            mfa_totp, sts_client)
    finally:
        teardown(user, virtual_mfa_device, role)
        print("Thanks for watching!")
# snippet-end:[python.example_code.sts.Scenario_AssumeRoleMfa_demo]


if __name__ == '__main__':
    usage_demo()
