# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the AWS Organizations API
to create and management policies for an organization.
"""

import argparse
import json
import logging
import pprint
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.organizations.CreatePolicy]
def create_policy(name, description, content, policy_type, orgs_client):
    """
    Creates a policy.

    :param name: The name of the policy.
    :param description: The description of the policy.
    :param content: The policy content as a dict. This is converted to JSON before
                    it is sent to AWS. The specific format depends on the policy type.
    :param policy_type: The type of the policy.
    :param orgs_client: The Boto3 Organizations client.
    :return: The newly created policy.
    """
    try:
        response = orgs_client.create_policy(
            Name=name, Description=description, Content=json.dumps(content),
            Type=policy_type)
        policy = response['Policy']
        logger.info("Created policy %s.", name)
    except ClientError:
        logger.exception("Couldn't create policy %s.", name)
        raise
    else:
        return policy
# snippet-end:[python.example_code.organizations.CreatePolicy]


# snippet-start:[python.example_code.organizations.ListPolicies]
def list_policies(policy_filter, orgs_client):
    """
    Lists the policies for the account, limited to the specified filter.

    :param policy_filter: The kind of policies to return.
    :param orgs_client: The Boto3 Organizations client.
    :return: The list of policies found.
    """
    try:
        response = orgs_client.list_policies(Filter=policy_filter)
        policies = response['Policies']
        logger.info("Found %s %s policies.", len(policies), policy_filter)
    except ClientError:
        logger.exception("Couldn't get %s policies.", policy_filter)
        raise
    else:
        return policies
# snippet-end:[python.example_code.organizations.ListPolicies]


# snippet-start:[python.example_code.organizations.DescribePolicy]
def describe_policy(policy_id, orgs_client):
    """
    Describes a policy.

    :param policy_id: The ID of the policy to describe.
    :param orgs_client: The Boto3 Organizations client.
    :return: The description of the policy.
    """
    try:
        response = orgs_client.describe_policy(PolicyId=policy_id)
        policy = response['Policy']
        logger.info("Got policy %s.", policy_id)
    except ClientError:
        logger.exception("Couldn't get policy %s.", policy_id)
        raise
    else:
        return policy
# snippet-end:[python.example_code.organizations.DescribePolicy]


# snippet-start:[python.example_code.organizations.AttachPolicy]
def attach_policy(policy_id, target_id, orgs_client):
    """
    Attaches a policy to a target. The target is an organization root, account, or
    organizational unit.

    :param policy_id: The ID of the policy to attach.
    :param target_id: The ID of the resources to attach the policy to.
    :param orgs_client: The Boto3 Organizations client.
    """
    try:
        orgs_client.attach_policy(PolicyId=policy_id, TargetId=target_id)
        logger.info("Attached policy %s to target %s.", policy_id, target_id)
    except ClientError:
        logger.exception(
            "Couldn't attach policy %s to target %s.", policy_id, target_id)
        raise
# snippet-end:[python.example_code.organizations.AttachPolicy]


# snippet-start:[python.example_code.organizations.DetachPolicy]
def detach_policy(policy_id, target_id, orgs_client):
    """
    Detaches a policy from a target.

    :param policy_id: The ID of the policy to detach.
    :param target_id: The ID of the resource where the policy is currently attached.
    :param orgs_client: The Boto3 Organizations client.
    """
    try:
        orgs_client.detach_policy(PolicyId=policy_id, TargetId=target_id)
        logger.info("Detached policy %s from target %s.", policy_id, target_id)
    except ClientError:
        logger.exception(
            "Couldn't detach policy %s from target %s.", policy_id, target_id)
        raise
# snippet-end:[python.example_code.organizations.DetachPolicy]


# snippet-start:[python.example_code.organizations.DeletePolicy]
def delete_policy(policy_id, orgs_client):
    """
    Deletes a policy.

    :param policy_id: The ID of the policy to delete.
    :param orgs_client: The Boto3 Organizations client.
    """
    try:
        orgs_client.delete_policy(PolicyId=policy_id)
        logger.info("Deleted policy %s.", policy_id)
    except ClientError:
        logger.exception(
            "Couldn't delete policy %s.", policy_id)
        raise
# snippet-end:[python.example_code.organizations.DeletePolicy]


def usage_demo(target_id):
    """
    Shows how to create an AWS Organizations policy and perform management functions
    on it. At the end of the demo, the policy is deleted.

    :param target_id: The ID of a target resource. When specified, the policy is
                      attached to and detached from this resource during the demo.
                      Otherwise, the attach and detach portion of the demo is skipped.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    orgs_client = boto3.client('organizations')

    print('-'*88)
    print("Welcome to the AWS Organizations policies demo.")
    print('-'*88)

    tag_policy_content = {
        'tags': {
            'CostCenter': {
                'tag_key': {'@@assign': 'CostCenter'},
                'tag_value': {'@@assign': ['AWS2', 'AWS']},
                'enforced_for': {
                    '@@assign': ['ec2:instance', 'ec2:volume']}}}
    }
    policy = create_policy(
        'AWS demo policy', 'Demonstrating AWS Organizations policies.',
        tag_policy_content, 'TAG_POLICY', orgs_client)
    print(f"Created policy {policy['PolicySummary']['Name']} with "
          f"ID {policy['PolicySummary']['Id']}.")

    policies = list_policies('TAG_POLICY', orgs_client)
    print("Current policies for this account:")
    pprint.pprint(policies)

    print("Full descriptions for policies:")
    for pol in policies:
        pprint.pprint(pol)

    policy_id = policy['PolicySummary']['Id']
    if target_id is not None:
        print(f"Attaching policy {policy_id} to {target_id}.")
        attach_policy(policy_id, target_id, orgs_client)
        print(f"Detaching policy {policy_id} from {target_id}.")
        detach_policy(policy_id, target_id, orgs_client)
    else:
        print("Target root or account not specified, skipping policy attach.")

    print(f"Deleting policy {policy['PolicySummary']['Name']}")
    delete_policy(policy_id, orgs_client)

    print("Thanks for watching!")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--target', help="The ID of the target organization root or account to attach "
                         "the test policy to.")
    args = parser.parse_args()
    usage_demo(args.target)
