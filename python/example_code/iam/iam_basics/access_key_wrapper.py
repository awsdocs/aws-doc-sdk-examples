# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS Identity and Access Management (IAM) access keys.
"""

# snippet-start:[python.example_code.iam.access_key_wrapper.imports]
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

iam = boto3.resource('iam')
# snippet-end:[python.example_code.iam.access_key_wrapper.imports]


# snippet-start:[python.example_code.iam.CreateAccessKey]
def create_key(user_name):
    """
    Creates an access key for the specified user. Each user can have a
    maximum of two keys.

    :param user_name: The name of the user.
    :return: The created access key.
    """
    try:
        key_pair = iam.User(user_name).create_access_key_pair()
        logger.info(
            "Created access key pair for %s. Key ID is %s.",
            key_pair.user_name, key_pair.id)
    except ClientError:
        logger.exception("Couldn't create access key pair for %s.", user_name)
        raise
    else:
        return key_pair
# snippet-end:[python.example_code.iam.CreateAccessKey]


# snippet-start:[python.example_code.iam.DeleteAccessKey]
def delete_key(user_name, key_id):
    """
    Deletes a user's access key.

    :param user_name: The user that owns the key.
    :param key_id: The ID of the key to delete.
    """

    try:
        key = iam.AccessKey(user_name, key_id)
        key.delete()
        logger.info(
            "Deleted access key %s for %s.", key.id, key.user_name)
    except ClientError:
        logger.exception("Couldn't delete key %s for %s", key_id, user_name)
        raise
# snippet-end:[python.example_code.iam.DeleteAccessKey]


# snippet-start:[python.example_code.iam.GetAccessKeyLastUsed]
def get_last_use(key_id):
    """
    Gets information about when and how a key was last used.

    :param key_id: The ID of the key to look up.
    :return: Information about the key's last use.
    """
    try:
        response = iam.meta.client.get_access_key_last_used(AccessKeyId=key_id)
        last_used_date = response['AccessKeyLastUsed'].get('LastUsedDate', None)
        last_service = response['AccessKeyLastUsed'].get('ServiceName', None)
        logger.info(
            "Key %s was last used by %s on %s to access %s.", key_id,
            response['UserName'], last_used_date, last_service)
    except ClientError:
        logger.exception("Couldn't get last use of key %s.", key_id)
        raise
    else:
        return response
# snippet-end:[python.example_code.iam.GetAccessKeyLastUsed]


# snippet-start:[python.example_code.iam.ListAccessKeys]
def list_keys(user_name):
    """
    Lists the keys owned by the specified user.

    :param user_name: The name of the user.
    :return: The list of keys owned by the user.
    """
    try:
        keys = list(iam.User(user_name).access_keys.all())
        logger.info("Got %s access keys for %s.", len(keys), user_name)
    except ClientError:
        logger.exception("Couldn't get access keys for %s.", user_name)
        raise
    else:
        return keys
# snippet-end:[python.example_code.iam.ListAccessKeys]


# snippet-start:[python.example_code.iam.UpdateAccessKey]
def update_key(user_name, key_id, activate):
    """
    Updates the status of a key.

    :param user_name: The user that owns the key.
    :param key_id: The ID of the key to update.
    :param activate: When True, the key is activated. Otherwise, the key is deactivated.
    """

    try:
        key = iam.User(user_name).AccessKey(key_id)
        if activate:
            key.activate()
        else:
            key.deactivate()
        logger.info("%s key %s.", 'Activated' if activate else 'Deactivated', key_id)
    except ClientError:
        logger.exception(
            "Couldn't %s key %s.", 'Activate' if activate else 'Deactivate', key_id)
        raise
# snippet-end:[python.example_code.iam.UpdateAccessKey]


# snippet-start:[python.example_code.iam.Scenario_ManageAccessKeys]
def usage_demo():
    """Shows how to create and manage access keys."""
    def print_keys():
        """Gets and prints the current keys for a user."""
        current_keys = list_keys(current_user_name)
        print("The current user's keys are now:")
        print(*[f"{key.id}: {key.status}" for key in current_keys], sep='\n')

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('-'*88)
    print("Welcome to the AWS Identity and Account Management access key demo.")
    print('-'*88)
    current_user_name = iam.CurrentUser().user_name
    print(f"This demo creates an access key for the current user "
          f"({current_user_name}), manipulates the key in a few ways, and then "
          f"deletes it.")
    all_keys = list_keys(current_user_name)
    if len(all_keys) == 2:
        print("The current user already has the maximum of 2 access keys. To run "
              "this demo, either delete one of the access keys or use a user "
              "that has only 1 access key.")
    else:
        new_key = create_key(current_user_name)
        print(f"Created a new key with id {new_key.id} and secret {new_key.secret}.")
        print_keys()
        existing_key = next(key for key in all_keys if key != new_key)
        last_use = get_last_use(existing_key.id)['AccessKeyLastUsed']
        print(f"Key {all_keys[0].id} was last used to access {last_use['ServiceName']} "
              f"on {last_use['LastUsedDate']}")
        update_key(current_user_name, new_key.id, False)
        print(f"Key {new_key.id} is now deactivated.")
        print_keys()
        delete_key(current_user_name, new_key.id)
        print_keys()
        print("Thanks for watching!")
# snippet-end:[python.example_code.iam.Scenario_ManageAccessKeys]


if __name__ == '__main__':
    usage_demo()
