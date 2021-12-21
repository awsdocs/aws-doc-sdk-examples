# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Key Management Service (AWS KMS)
to manage the security policy for a key.
"""

# snippet-start:[python.example_code.kms.Scenario_ManageKeyPolicies]
import json
import logging
from pprint import pprint
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kms.KeyPolicy]
class KeyPolicy:
    def __init__(self, kms_client):
        self.kms_client = kms_client
# snippet-end:[python.example_code.kms.KeyPolicy]

# snippet-start:[python.example_code.kms.ListKeyPolicies]
    def list_policies(self, key_id):
        """
        Lists the names of the policies for a key.

        :param key_id: The ARN or ID of the key to query.
        """
        try:
            policy_names = self.kms_client.list_key_policies(KeyId=key_id)['PolicyNames']
        except ClientError as err:
            logging.error(
                "Couldn't list your policies. Here's why: %s", err.response['Error']['Message'])
        else:
            print(f"The policies for key {key_id} are:")
            pprint(policy_names)
# snippet-end:[python.example_code.kms.ListKeyPolicies]

# snippet-start:[python.example_code.kms.GetKeyPolicy]
    def get_policy(self, key_id):
        """
        Gets the policy of a key.

        :param key_id: The ARN or ID of the key to query.
        :return: The key policy as a dict.
        """
        if key_id != '':
            try:
                response = self.kms_client.get_key_policy(
                    KeyId=key_id, PolicyName='default')
                policy = json.loads(response['Policy'])
            except ClientError as err:
                logger.error(
                    "Couldn't get policy for key %s. Here's why: %s",
                    key_id, err.response['Error']['Message'])
            else:
                pprint(policy)
                return policy
        else:
            print("Skipping get policy demo.")
# snippet-end:[python.example_code.kms.GetKeyPolicy]

# snippet-start:[python.example_code.kms.PutKeyPolicy]
    def set_policy(self, key_id, policy):
        """
        Sets the policy of a key. Setting a policy entirely overwrites the existing
        policy, so care is taken to add a statement to the existing list of statements
        rather than simply writing a new policy.

        :param key_id: The ARN or ID of the key to set the policy to.
        :param policy: The existing policy of the key.
        """
        user = input("Enter the ARN of an IAM user to set as the principal on the policy: ")
        if key_id != '' and user != '':
            # The updated policy replaces the existing policy. Add a new statement to
            # the list along with the original policy statements.
            policy['Statement'].append({
                "Sid": "Allow access for ExampleUser",
                "Effect": "Allow",
                "Principal": {"AWS": user},
                "Action": [
                    "kms:Encrypt",
                    "kms:GenerateDataKey*",
                    "kms:Decrypt",
                    "kms:DescribeKey",
                    "kms:ReEncrypt*"],
                "Resource": "*"})
            try:
                self.kms_client.put_key_policy(
                    KeyId=key_id, PolicyName='default', Policy=json.dumps(policy))
            except ClientError as err:
                logger.error(
                    "Couldn't set policy for key %s. Here's why %s",
                    key_id, err.response['Error']['Message'])
            else:
                print(f"Set policy for key {key_id}.")
        else:
            print("Skipping set policy demo.")
# snippet-end:[python.example_code.kms.PutKeyPolicy]


def key_policies(kms_client):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the AWS Key Management Service (AWS KMS) key policies demo.")
    print('-'*88)

    key_id = input("Enter a key ID or ARN to start the demo: ")
    if key_id == '':
        print("A key is required to run this demo.")
        return

    key_policy = KeyPolicy(kms_client)
    key_policy.list_policies(key_id)
    print('-'*88)
    policy = key_policy.get_policy(key_id)
    print('-'*88)
    if policy is not None:
        key_policy.set_policy(key_id, policy)

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        key_policies(boto3.client('kms'))
    except Exception:
        logging.exception("Something went wrong with the demo!")
# snippet-end:[python.example_code.kms.Scenario_ManageKeyPolicies]
