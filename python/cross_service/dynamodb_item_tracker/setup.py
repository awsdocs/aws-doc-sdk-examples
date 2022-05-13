# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to deploy and destroy a
CloudFormation stack that manages resources for the example.
"""

import argparse

import boto3


def deploy(stack_name, cf_resource):
    """
    Deploys prerequisite resources used by the example. The resources are
    defined in the associated `setup.yaml` CloudFormation script and are deployed
    as a CloudFormation stack so they can be easily managed and destroyed.

    As a side effect, this function creates a `config.py` file that is used by the
    app. The file contains a TABLE_NAME option that specifies the name of the
    DynamoDB table created by the script.

    :param stack_name: The name of the CloudFormation stack.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    with open('setup.yaml') as setup_file:
        setup_template = setup_file.read()
    print(f"Creating {stack_name}.")
    stack = cf_resource.create_stack(
        StackName=stack_name,
        TemplateBody=setup_template,
        Capabilities=['CAPABILITY_NAMED_IAM'])
    print("Waiting for stack to deploy.  This typically takes a minute or two.")
    waiter = cf_resource.meta.client.get_waiter('stack_create_complete')
    waiter.wait(StackName=stack.name)
    stack.load()
    print(f"Stack status: {stack.stack_status}")
    print("Created resources:")
    for resource in stack.resource_summaries.all():
        print(f"\t{resource.resource_type}, {resource.physical_resource_id}")
    print("Outputs:")
    for oput in stack.outputs:
        print(f"\t{oput['OutputKey']}: {oput['OutputValue']}")
        if oput['OutputKey'] == 'TableName':
            with open('config.py', 'w') as config:
                config.write(f"TABLE_NAME = '{oput['OutputValue']}'\n")


def destroy(stack, cf_resource):
    """
    Destroys the resources managed by the CloudFormation stack, and the CloudFormation
    stack itself.

    :param stack: The CloudFormation stack that manages the demo resources.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    print(f"Deleting {stack.name}.")
    stack.delete()
    print("Waiting for stack removal.")
    waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
    waiter.wait(StackName=stack.name)
    print("Stack delete complete.")


def main():
    parser = argparse.ArgumentParser(
        description="Runs the Amazon DynamoDB Item Tracker demo. Run this script with the "
                    "'deploy' flag to deploy prerequisite resources. Run with the "
                    "'destroy' flag to clean up all resources.")
    parser.add_argument(
        'action', choices=['deploy', 'destroy'],
        help="Indicates the action the script performs.")
    args = parser.parse_args()

    print('-'*88)
    print("Welcome to the Amazon DynamoDB Item Tracker demo!")
    print('-'*88)

    cf_resource = boto3.resource('cloudformation')
    stack = cf_resource.Stack('doc-example-work-item-tracker-stack')

    if args.action == 'deploy':
        print("Deploying prerequisite resources for the demo.")
        deploy(stack.name, cf_resource)
        print('-'*88)
        print("To clean up all AWS resources created for the demo, run this script "
              "again with the 'destroy' flag.")
    elif args.action == 'destroy':
        print("Destroying AWS resources created for the demo.")
        destroy(stack, cf_resource)

    print('-'*88)


if __name__ == '__main__':
    main()
