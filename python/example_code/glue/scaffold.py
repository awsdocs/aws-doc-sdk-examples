# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Deploys and destroys scaffold resources by using an AWS CloudFormation stack.
"""

import argparse

import boto3
from botocore.exceptions import ClientError


def deploy(cloud_formation_script, stack_name, cf_resource):
    """
    Deploys scaffold resources used by the example. The resources are
    defined in the CloudFormation script. They're deployed as a CloudFormation stack
    so you can manage and destroy them by using CloudFormation actions.

    :param cloud_formation_script: The path to a CloudFormation script.
    :param stack_name: The name of the CloudFormation stack.
    :param cf_resource: A Boto3 CloudFormation resource.
    :return: A dict of outputs from the stack.
    """
    with open(cloud_formation_script) as setup_file:
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
    outputs = {}
    for oput in stack.outputs:
        outputs[oput['OutputKey']] = oput['OutputValue']
        print(f"\t{oput['OutputKey']}: {oput['OutputValue']}")
    return outputs


def destroy(stack, cf_resource, s3_resource):
    """
    Destroys the resources managed by the CloudFormation stack, and the CloudFormation
    stack itself.

    :param stack: The CloudFormation stack that manages the example resources.
    :param cf_resource: A Boto3 CloudFormation resource.
    :param s3_resource: A Boto3 S3 resource.
    """
    bucket_name = None
    for oput in stack.outputs:
        if oput['OutputKey'] == 'BucketName':
            bucket_name = oput['OutputValue']
    if bucket_name is not None:
        print(f"Deleting all objects in bucket {bucket_name}.")
        s3_resource.Bucket(bucket_name).objects.delete()
    print(f"Deleting {stack.name}.")
    stack.delete()
    print("Waiting for stack removal.")
    waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
    waiter.wait(StackName=stack.name)
    print("Stack delete complete.")


def main():
    parser = argparse.ArgumentParser(
        description="Deploys and destroys scaffold resources for the 'Getting started "
                    "with crawlers and jobs' scenario. Run with the 'deploy' action to "
                    "deploy resources or with the 'destroy' action to destroy resources.")
    parser.add_argument(
        'action', choices=['deploy', 'destroy'],
        help="Indicates the action that the script performs.")
    parser.add_argument(
        '--script', default='setup_scenario_getting_started.yaml',
        help="The name of the CloudFormation script to use to deploy resources.")
    args = parser.parse_args()

    print('-'*88)
    print("Welcome to the AWS Glue getting started with crawlers and jobs scenario.")
    print('-'*88)

    cf_resource = boto3.resource('cloudformation')
    stack = cf_resource.Stack('doc-example-glue-scenario-stack')

    try:
        if args.action == 'deploy':
            print("Deploying scaffold resources for the example.")
            outputs = deploy(args.script, stack.name, cf_resource)
            print('-'*88)
            print("To run the scenario, pass the role and bucket names to it: ")
            print(f"\tpython scenario_getting_started_crawlers_and_jobs.py "
                  f"{outputs['RoleName']} {outputs['BucketName']}")
            print('-'*88)
            print("To clean up all AWS resources created for the example, run this script "
                  "again with the 'destroy' flag.")
        elif args.action == 'destroy':
            print("Destroying scaffold resources created for the example.")
            destroy(stack, cf_resource, boto3.resource('s3'))
    except ClientError as err:
        print(f"Something went wrong while trying to {args.action} the stack:")
        print(f"{err.response['Error']['Code']}: {err.response['Error']['Message']}")

    print('-'*88)


if __name__ == '__main__':
    main()
