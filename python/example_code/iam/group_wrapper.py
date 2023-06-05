# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS Identity and Access Management (IAM) groups.
"""

# snippet-start:[python.example_code.iam.group_wrapper.imports]
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
iam = boto3.resource('iam')
# snippet-end:[python.example_code.iam.group_wrapper.imports]


# snippet-start:[python.example_code.iam.ListGroups]
def list_groups(count):
    """
    Lists the specified number of groups for the account.

    :param count: The number of groups to list.
    """
    try:
        for group in iam.groups.limit(count):
            logger.info("Group: %s", group.name)
    except ClientError:
        logger.exception("Couldn't list groups for the account.")
        raise
# snippet-end:[python.example_code.iam.ListGroups]


def usage_demo():
    print('-'*88)
    print("Welcome to the IAM groups demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print("Listing up to 10 groups for the account.")
    list_groups(10)

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    usage_demo()
