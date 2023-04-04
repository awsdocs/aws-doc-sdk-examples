# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS Identity and Access Management (IAM) users.
"""

# snippet-start:[python.example_code.iam.user_wrapper.imports]
import logging
import time

import boto3
from botocore.exceptions import ClientError

import access_key_wrapper
import policy_wrapper

logger = logging.getLogger(__name__)
iam = boto3.resource('iam')
# snippet-end:[python.example_code.iam.user_wrapper.imports]


# snippet-start:[python.example_code.iam.CreateUser]
def create_user(user_name):
    """
    Creates a user. By default, a user has no permissions or access keys.

    :param user_name: The name of the user.
    :return: The newly created user.
    """
    try:
        user = iam.create_user(UserName=user_name)
        logger.info("Created user %s.", user.name)
    except ClientError:
        logger.exception("Couldn't create user %s.", user_name)
        raise
    else:
        return user
# snippet-end:[python.example_code.iam.CreateUser]


# snippet-start:[python.example_code.iam.DeleteUser]
def delete_user(user_name):
    """
    Deletes a user. Before a user can be deleted, all associated resources,
    such as access keys and policies, must be deleted or detached.

    :param user_name: The name of the user.
    """
    try:
        iam.User(user_name).delete()
        logger.info("Deleted user %s.", user_name)
    except ClientError:
        logger.exception("Couldn't delete user %s.", user_name)
        raise
# snippet-end:[python.example_code.iam.DeleteUser]


# snippet-start:[python.example_code.iam.ListUsers]
def list_users():
    """
    Lists the users in the current account.

    :return: The list of users.
    """
    try:
        users = list(iam.users.all())
        logger.info("Got %s users.", len(users))
    except ClientError:
        logger.exception("Couldn't get users.")
        raise
    else:
        return users
# snippet-end:[python.example_code.iam.ListUsers]


# snippet-start:[python.example_code.iam.UpdateUser]
def update_user(user_name, new_user_name):
    """
    Updates a user's name.

    :param user_name: The current name of the user to update.
    :param new_user_name: The new name to assign to the user.
    :return: The updated user.
    """
    try:
        user = iam.User(user_name)
        user.update(NewUserName=new_user_name)
        logger.info("Renamed %s to %s.", user_name, new_user_name)
    except ClientError:
        logger.exception("Couldn't update name for user %s.", user_name)
        raise
    return user
# snippet-end:[python.example_code.iam.UpdateUser]


# snippet-start:[python.example_code.iam.AttachUserPolicy]
def attach_policy(user_name, policy_arn):
    """
    Attaches a policy to a user.

    :param user_name: The name of the user.
    :param policy_arn: The Amazon Resource Name (ARN) of the policy.
    """
    try:
        iam.User(user_name).attach_policy(PolicyArn=policy_arn)
        logger.info("Attached policy %s to user %s.", policy_arn, user_name)
    except ClientError:
        logger.exception("Couldn't attach policy %s to user %s.", policy_arn, user_name)
        raise
# snippet-end:[python.example_code.iam.AttachUserPolicy]


# snippet-start:[python.example_code.iam.DetachUserPolicy]
def detach_policy(user_name, policy_arn):
    """
    Detaches a policy from a user.

    :param user_name: The name of the user.
    :param policy_arn: The Amazon Resource Name (ARN) of the policy.
    """
    try:
        iam.User(user_name).detach_policy(PolicyArn=policy_arn)
        logger.info("Detached policy %s from user %s.", policy_arn, user_name)
    except ClientError:
        logger.exception(
            "Couldn't detach policy %s from user %s.", policy_arn, user_name)
        raise
# snippet-end:[python.example_code.iam.DetachUserPolicy]


# snippet-start:[python.example_code.iam.Scenario_UserPolicies]
def usage_demo():
    """
    Shows how to manage users, keys, and policies.
    This demonstration creates two users: one user who can put and get objects in an
    Amazon S3 bucket, and another user who can only get objects from the bucket.
    The demo then shows how the users can perform only the actions they are permitted
    to perform.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('-'*88)
    print("Welcome to the AWS Identity and Account Management user demo.")
    print('-'*88)
    print("Users can have policies and roles attached to grant them specific "
          "permissions.")
    s3 = boto3.resource('s3')
    bucket = s3.create_bucket(
        Bucket=f'demo-iam-bucket-{time.time_ns()}',
        CreateBucketConfiguration={
            'LocationConstraint': s3.meta.client.meta.region_name
        }
    )
    print(f"Created an Amazon S3 bucket named {bucket.name}.")
    user_read_writer = create_user('demo-iam-read-writer')
    user_reader = create_user('demo-iam-reader')
    print(f"Created two IAM users: {user_read_writer.name} and {user_reader.name}")
    update_user(user_read_writer.name, 'demo-iam-creator')
    update_user(user_reader.name, 'demo-iam-getter')
    users = list_users()
    user_read_writer = next(user for user in users if user.user_id == user_read_writer.user_id)
    user_reader = next(user for user in users if user.user_id == user_reader.user_id)
    print(f"Changed the names of the users to {user_read_writer.name} "
          f"and {user_reader.name}.")

    read_write_policy = policy_wrapper.create_policy(
        'demo-iam-read-write-policy',
        'Grants rights to create and get an object in the demo bucket.',
        ['s3:PutObject', 's3:GetObject'],
        f'arn:aws:s3:::{bucket.name}/*')
    print(f"Created policy {read_write_policy.policy_name} with ARN: {read_write_policy.arn}")
    print(read_write_policy.description)
    read_policy = policy_wrapper.create_policy(
        'demo-iam-read-policy',
        'Grants rights to get an object from the demo bucket.',
        's3:GetObject',
        f'arn:aws:s3:::{bucket.name}/*')
    print(f"Created policy {read_policy.policy_name} with ARN: {read_policy.arn}")
    print(read_policy.description)
    attach_policy(user_read_writer.name, read_write_policy.arn)
    print(f"Attached {read_write_policy.policy_name} to {user_read_writer.name}.")
    attach_policy(user_reader.name, read_policy.arn)
    print(f"Attached {read_policy.policy_name} to {user_reader.name}.")

    user_read_writer_key = access_key_wrapper.create_key(user_read_writer.name)
    print(f"Created access key pair for {user_read_writer.name}.")
    user_reader_key = access_key_wrapper.create_key(user_reader.name)
    print(f"Created access key pair for {user_reader.name}.")

    s3_read_writer_resource = boto3.resource(
        's3',
        aws_access_key_id=user_read_writer_key.id,
        aws_secret_access_key=user_read_writer_key.secret)
    demo_object_key = f'object-{time.time_ns()}'
    demo_object = None
    while demo_object is None:
        try:
            demo_object = s3_read_writer_resource.Bucket(bucket.name).put_object(
                Key=demo_object_key, Body=b'AWS IAM demo object content!')
        except ClientError as error:
            if error.response['Error']['Code'] == 'InvalidAccessKeyId':
                print("Access key not yet available. Waiting...")
                time.sleep(1)
            else:
                raise
    print(f"Put {demo_object_key} into {bucket.name} using "
          f"{user_read_writer.name}'s credentials.")

    read_writer_object = s3_read_writer_resource.Bucket(
        bucket.name).Object(demo_object_key)
    read_writer_content = read_writer_object.get()['Body'].read()
    print(f"Got object {read_writer_object.key} using read-writer user's credentials.")
    print(f"Object content: {read_writer_content}")

    s3_reader_resource = boto3.resource(
        's3',
        aws_access_key_id=user_reader_key.id,
        aws_secret_access_key=user_reader_key.secret)
    demo_content = None
    while demo_content is None:
        try:
            demo_object = s3_reader_resource.Bucket(bucket.name).Object(demo_object_key)
            demo_content = demo_object.get()['Body'].read()
            print(f"Got object {demo_object.key} using reader user's credentials.")
            print(f"Object content: {demo_content}")
        except ClientError as error:
            if error.response['Error']['Code'] == 'InvalidAccessKeyId':
                print("Access key not yet available. Waiting...")
                time.sleep(1)
            else:
                raise

    try:
        demo_object.delete()
    except ClientError as error:
        if error.response['Error']['Code'] == 'AccessDenied':
            print('-'*88)
            print("Tried to delete the object using the reader user's credentials. "
                  "Got expected AccessDenied error because the reader is not "
                  "allowed to delete objects.")
            print('-'*88)

    access_key_wrapper.delete_key(user_reader.name, user_reader_key.id)
    detach_policy(user_reader.name, read_policy.arn)
    policy_wrapper.delete_policy(read_policy.arn)
    delete_user(user_reader.name)
    print(f"Deleted keys, detached and deleted policy, and deleted {user_reader.name}.")

    access_key_wrapper.delete_key(user_read_writer.name, user_read_writer_key.id)
    detach_policy(user_read_writer.name, read_write_policy.arn)
    policy_wrapper.delete_policy(read_write_policy.arn)
    delete_user(user_read_writer.name)
    print(f"Deleted keys, detached and deleted policy, and deleted {user_read_writer.name}.")

    bucket.objects.delete()
    bucket.delete()
    print(f"Emptied and deleted {bucket.name}.")
    print("Thanks for watching!")
# snippet-end:[python.example_code.iam.Scenario_UserPolicies]


if __name__ == '__main__':
    usage_demo()
