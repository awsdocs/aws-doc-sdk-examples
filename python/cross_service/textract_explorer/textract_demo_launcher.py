# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Textract to detect text,
form, and table elements in a document image. The input image and Textract output are
shown in a Tkinter application that lets you explore the detected elements.
"""

import argparse
from io import BytesIO
import logging
import boto3
from textract_wrapper import TextractWrapper
from textract_app import TextractExplorer

logger = logging.getLogger(__name__)


def deploy(stack_name, cf_resource):
    """
    Deploys prerequisite resources used by the `usage_demo` script. The resources are
    defined in the associated `setup.yaml` AWS CloudFormation script and are deployed
    as a CloudFormation stack so they can be easily managed and destroyed.

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


def usage_demo(outputs):
    """
    Launches the Textract Explorer Tkinter application with a default document image.
    """
    s3_resource = boto3.resource('s3')
    bucket = s3_resource.Bucket(
        'textract-public-assets-' + s3_resource.meta.client.meta.region_name)
    default_image_name = 'default_document_3.png'
    default_image_bytes = BytesIO()
    bucket.download_fileobj(default_image_name, default_image_bytes)
    twrapper = TextractWrapper(
        boto3.client('textract'), boto3.resource('s3'), boto3.resource('sqs'))
    TextractExplorer(twrapper, outputs, default_image_name, default_image_bytes)


def destroy(stack, outputs, cf_resource):
    """
    Destroys the resources managed by the CloudFormation stack, and the CloudFormation
    stack itself.

    :param stack: The CloudFormation stack that manages the demo resources.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    print(f"Emptying bucket {outputs['BucketName']}.")
    boto3.resource('s3').Bucket(outputs['BucketName']).objects.delete()
    print(f"Deleting {stack.name}.")
    stack.delete()
    print("Waiting for stack removal.")
    waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
    waiter.wait(StackName=stack.name)
    print("Stack delete complete.")


def main():
    parser = argparse.ArgumentParser(
        description="Runs the Amazon Textract demo. Run this script with the "
                    "'deploy' flag to deploy prerequisite resources, then with the "
                    "'demo' flag to see example usage. Run with the 'destroy' flag to "
                    "clean up all resources.")
    parser.add_argument(
        'action', choices=['deploy', 'demo', 'destroy'],
        help="Indicates the action the script performs.")
    args = parser.parse_args()

    print('-'*88)
    print("Welcome to the Amazon Textract demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    cf_resource = boto3.resource('cloudformation')
    stack = cf_resource.Stack('textract-example-s3-sns-sqs')
    if args.action in ('demo', 'destroy'):
        outputs = {o['OutputKey']: o['OutputValue'] for o in stack.outputs}

    if args.action == 'deploy':
        print("Deploying prerequisite resources for the demo.")
        deploy(stack.name, cf_resource)
        print('-'*88)
        print("To see example usage, run the script again with the 'demo' flag.")
    elif args.action == 'demo':
        print('-'*88)
        print("Demonstrating how to use Amazon Textract.")
        print('-'*88)
        usage_demo(outputs)
        print('-'*88)
        print("To clean up all AWS resources created for the demo, run this script "
              "again with the 'destroy' flag.")
    elif args.action == 'destroy':
        print("Destroying AWS resources created for the demo.")
        destroy(stack, outputs, cf_resource)

    print('-'*88)


if __name__ == '__main__':
    main()
