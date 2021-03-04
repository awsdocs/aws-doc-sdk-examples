# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS Identity and Access Management (IAM) users.
"""

import logging
import time

import boto3
from botocore.exceptions import ClientError

import access_key_wrapper
import policy_wrapper

logger = logging.getLogger(__name__)
iam = boto3.resource('iam')


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


def usage_demo():
    """
    Shows how to manage users, keys, and policies.
    This demonstration creates two users: one user who can only put objects in an
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
    user_writer = create_user('demo-iam-writer')
    user_reader = create_user('demo-iam-reader')
    print(f"Created two IAM users: {user_writer.name} and {user_reader.name}")
    update_user(user_writer.name, 'demo-iam-creator')
    update_user(user_reader.name, 'demo-iam-getter')
    users = list_users()
    user_writer = next(user for user in users if user.user_id == user_writer.user_id)
    user_reader = next(user for user in users if user.user_id == user_reader.user_id)
    print(f"Changed the names of the users to {user_writer.name} "
          f"and {user_reader.name}.")

    write_policy = policy_wrapper.create_policy(
        'demo-iam-write-policy',
        'Grants rights to create an object in the demo bucket.',
        's3:PutObject',
        f'arn:aws:s3:::{bucket.name}/*')
    print(f"Created policy {write_policy.policy_name} with ARN: {write_policy.arn}")
    print(write_policy.description)
    read_policy = policy_wrapper.create_policy(
        'demo-iam-read-policy',
        'Grants rights to get an object from the demo bucket.',
        's3:GetObject',
        f'arn:aws:s3:::{bucket.name}/*')
    print(f"Created policy {read_policy.policy_name} with ARN: {read_policy.arn}")
    print(read_policy.description)
    attach_policy(user_writer.name, write_policy.arn)
    print(f"Attached {write_policy.policy_name} to {user_writer.name}.")
    attach_policy(user_reader.name, read_policy.arn)
    print(f"Attached {read_policy.policy_name} to {user_reader.name}.")

    user_writer_key = access_key_wrapper.create_key(user_writer.name)
    print(f"Created access key pair for {user_writer.name}.")
    user_reader_key = access_key_wrapper.create_key(user_reader.name)
    print(f"Created access key pair for {user_reader.name}.")

    s3_writer_resource = boto3.resource(
        's3',
        aws_access_key_id=user_writer_key.id,
        aws_secret_access_key=user_writer_key.secret)
    demo_object_key = f'object-{time.time_ns()}'
    demo_object = None
    while demo_object is None:
        try:
            demo_object = s3_writer_resource.Bucket(bucket.name).put_object(
                Key=demo_object_key, Body=b'AWS IAM demo object content!')
        except ClientError as error:
            if error.response['Error']['Code'] == 'InvalidAccessKeyId':
                print("Access key not yet available. Waiting...")
                time.sleep(1)
            else:
                raise
    print(f"Put {demo_object_key} into {bucket.name} using "
          f"{user_writer.name}'s credentials.")

    try:
        s3_writer_resource.Bucket(bucket.name).Object(demo_object_key).get()
    except ClientError as error:
        if error.response['Error']['Code'] == 'AccessDenied':
            print("Tried to get the demo object with the writer user's credentials. "
                  "Got expected AccessDenied error because the writer is not allowed "
                  "to get objects.")
        else:
            raise
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
            print("Tried to delete the object using the reader user's credentials. "
                  "Got expected AccessDenied error because the reader is not "
                  "allowed to delete objects.")

    access_key_wrapper.delete_key(user_reader.name, user_reader_key.id)
    detach_policy(user_reader.name, read_policy.arn)
    policy_wrapper.delete_policy(read_policy.arn)
    delete_user(user_reader.name)
    print(f"Deleted keys, detached and deleted policy, and deleted {user_reader.name}.")

    access_key_wrapper.delete_key(user_writer.name, user_writer_key.id)
    detach_policy(user_writer.name, write_policy.arn)
    policy_wrapper.delete_policy(write_policy.arn)
    delete_user(user_writer.name)
    print(f"Deleted keys, detached and deleted policy, and deleted {user_writer.name}.")

    bucket.objects.delete()
    bucket.delete()
    print(f"Emptied and deleted {bucket.name}.")
    print("Thanks for watching!")


if __name__ == '__main__':
    usage_demo()
