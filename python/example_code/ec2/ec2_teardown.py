# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Elastic Compute Cloud
(Amazon EC2) API to terminate an instance and clean up additional resources.
"""

import logging
import os
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
ec2 = boto3.resource('ec2')


# snippet-start:[python.example_code.ec2.DeleteKeyPair]
def delete_key_pair(key_name, key_file_name):
    """
    Deletes a key pair and the specified private key file.

    :param key_name: The name of the key pair to delete.
    :param key_file_name: The local file name of the private key file.
    """
    try:
        ec2.KeyPair(key_name).delete()
        os.remove(key_file_name)
        logger.info("Deleted key %s and private key file %s.", key_name, key_file_name)
    except ClientError:
        logger.exception("Couldn't delete key %s.", key_name)
        raise
# snippet-end:[python.example_code.ec2.DeleteKeyPair]


# snippet-start:[python.example_code.ec2.DeleteSecurityGroup]
def delete_security_group(group_id):
    """
    Deletes a security group.

    :param group_id: The ID of the security group to delete.
    """
    try:
        ec2.SecurityGroup(group_id).delete()
        logger.info("Deleted security group %s.", group_id)
    except ClientError:
        logger.exception("Couldn't delete security group %s.", group_id)
        raise
# snippet-end:[python.example_code.ec2.DeleteSecurityGroup]


# snippet-start:[python.example_code.ec2.TerminateInstances]
def terminate_instance(instance_id):
    """
    Terminates an instance. The request returns immediately. To wait for the
    instance to terminate, use Instance.wait_until_terminated().

    :param instance_id: The ID of the instance to terminate.
    """
    try:
        ec2.Instance(instance_id).terminate()
        logger.info("Terminating instance %s.", instance_id)
    except ClientError:
        logging.exception("Couldn't terminate instance %s.", instance_id)
        raise
# snippet-end:[python.example_code.ec2.TerminateInstances]
