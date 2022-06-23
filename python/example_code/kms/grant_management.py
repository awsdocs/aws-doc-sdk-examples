# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Key Management Service (AWS KMS)
to manage permission grants for keys.
"""

# snippet-start:[python.example_code.kms.Scenario_GrantManagement]
import logging
from pprint import pprint
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kms.GrantManager]
class GrantManager:
    def __init__(self, kms_client):
        self.kms_client = kms_client
# snippet-end:[python.example_code.kms.GrantManager]

# snippet-start:[python.example_code.kms.CreateGrant]
    def create_grant(self, key_id):
        """
        Creates a grant for a key that lets a user generate a symmetric data
        encryption key.

        :param key_id: The ARN or ID of the key.
        :return: The grant that is created.
        """
        user = input(
            f"Enter the ARN of an IAM user to grant that user GenerateDataKey "
            f"permissions on key {key_id}.")
        if user != '':
            try:
                grant = self.kms_client.create_grant(
                    KeyId=key_id, GranteePrincipal=user, Operations=['GenerateDataKey'])
            except ClientError as err:
                logger.error(
                    "Couldn't create a grant on key %s. Here's why: %s",
                    key_id, err.response['Error']['Message'])
            else:
                print(f"Grant created on key {key_id}.")
                return grant
        else:
            print("Skipping grant creation.")
# snippet-end:[python.example_code.kms.CreateGrant]

# snippet-start:[python.example_code.kms.ListGrants]
    def list_grants(self, key_id):
        """
        Lists grants for a key.

        :param key_id: The ARN or ID of the key to query.
        :return: The grants for the key.
        """
        answer = input(f"Ready to list grants on key {key_id} (y/n)? ")
        if answer.lower() == 'y':
            try:
                grants = self.kms_client.list_grants(KeyId=key_id)['Grants']
            except ClientError as err:
                logger.error(
                    "Couldn't list grants for key %s. Here's why: %s",
                    key_id, err.response['Error']['Message'])
            else:
                print(f"Grants for key {key_id}:")
                pprint(grants)
                return grants
# snippet-end:[python.example_code.kms.ListGrants]

# snippet-start:[python.example_code.kms.RetireGrant]
    def retire_grant(self, grant):
        """
        Retires a grant so that it can no longer be used.

        :param grant: The grant to retire.
        """
        try:
            self.kms_client.retire_grant(GrantToken=grant['GrantToken'])
        except ClientError as err:
            logger.error(
                "Couldn't retire grant %s. Here's why: %s",
                grant['GrantId'], err.response['Error']['Message'])
        else:
            print(f"Grant {grant['GrantId']} retired.")
# snippet-end:[python.example_code.kms.RetireGrant]

# snippet-start:[python.example_code.kms.RevokeGrant]
    def revoke_grant(self, key_id, grant):
        """
        Revokes a grant so that it can no longer be used.

        :param key_id: The ARN or ID of the key associated with the grant.
        :param grant: The grant to revoke.
        """
        try:
            self.kms_client.revoke_grant(KeyId=key_id, GrantId=grant['GrantId'])
        except ClientError as err:
            logger.error(
                "Couldn't revoke grant %s. Here's why: %s",
                grant['GrantId'], err.response['Error']['Message'])
        else:
            print(f"Grant {grant['GrantId']} revoked.")
# snippet-end:[python.example_code.kms.RevokeGrant]


def grant_management(kms_client):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the AWS Key Management Service (AWS KMS) grant management demo.")
    print('-'*88)

    key_id = input("Enter a key ID or ARN to start the demo: ")
    if key_id == '':
        print("A key is required to run this demo.")
        return

    grant_manager = GrantManager(kms_client)
    grant = grant_manager.create_grant(key_id)
    print('-'*88)
    grant_manager.list_grants(key_id)
    print('-'*88)
    if grant is not None:
        action = input("Let's remove the demo grant. Enter 'retire' or 'revoke': ")
        if action == 'retire':
            grant_manager.retire_grant(grant)
        elif action == 'revoke':
            grant_manager.revoke_grant(key_id, grant)
        else:
            print("Skipping grant removal.")

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        grant_management(boto3.client('kms'))
    except Exception:
        logging.exception("Something went wrong with the demo!")
# snippet-end:[python.example_code.kms.Scenario_GrantManagement]
