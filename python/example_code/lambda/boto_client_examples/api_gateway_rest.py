# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to use Amazon API Gateway to
create a REST API backed by a Lambda function.

Instead of using the low-level Boto3 client APIs shown in this example, you can use
AWS Chalice to more easily create a REST API.

    For a working code example, see the `lambda/chalice_examples/lambda_rest` example
    in this GitHub repo.

    For more information about AWS Chalice, see https://github.com/aws/chalice.
"""

import calendar
import datetime
import json
import logging
import time
import boto3
from botocore.exceptions import ClientError
import requests

import lambda_basics

logger = logging.getLogger(__name__)


def create_rest_api(
        apigateway_client, api_name, api_base_path, api_stage,
        account_id, lambda_client, lambda_function_arn):
    """
    Creates a REST API in Amazon API Gateway. The REST API is backed by the specified
    AWS Lambda function.

    The following is how the function puts the pieces together, in order:
    1. Creates a REST API in Amazon API Gateway.
    2. Creates a '/demoapi' resource in the REST API.
    3. Creates a method that accepts all HTTP actions and passes them through to
       the specified AWS Lambda function.
    4. Deploys the REST API to Amazon API Gateway.
    5. Adds a resource policy to the AWS Lambda function that grants permission
       to let Amazon API Gateway call the AWS Lambda function.

    :param apigateway_client: The Boto3 Amazon API Gateway client object.
    :param api_name: The name of the REST API.
    :param api_base_path: The base path part of the REST API URL.
    :param api_stage: The deployment stage of the REST API.
    :param account_id: The ID of the owning AWS account.
    :param lambda_client: The Boto3 AWS Lambda client object.
    :param lambda_function_arn: The Amazon Resource Name (ARN) of the AWS Lambda
                                function that is called by Amazon API Gateway to
                                handle REST requests.
    :return: The ID of the REST API. This ID is required by most Amazon API Gateway
             methods.
    """
    try:
        response = apigateway_client.create_rest_api(name=api_name)
        api_id = response['id']
        logger.info("Create REST API %s with ID %s.", api_name, api_id)
    except ClientError:
        logger.exception("Couldn't create REST API %s.", api_name)
        raise

    try:
        response = apigateway_client.get_resources(restApiId=api_id)
        root_id = next(item['id'] for item in response['items'] if item['path'] == '/')
        logger.info("Found root resource of the REST API with ID %s.", root_id)
    except ClientError:
        logger.exception("Couldn't get the ID of the root resource of the REST API.")
        raise

    try:
        response = apigateway_client.create_resource(
            restApiId=api_id, parentId=root_id, pathPart=api_base_path)
        base_id = response['id']
        logger.info("Created base path %s with ID %s.", api_base_path, base_id)
    except ClientError:
        logger.exception("Couldn't create a base path for %s.", api_base_path)
        raise

    try:
        apigateway_client.put_method(
            restApiId=api_id, resourceId=base_id, httpMethod='ANY',
            authorizationType='NONE')
        logger.info("Created a method that accepts all HTTP verbs for the base "
                    "resource.")
    except ClientError:
        logger.exception("Couldn't create a method for the base resource.")
        raise

    lambda_uri = \
        f'arn:aws:apigateway:{apigateway_client.meta.region_name}:' \
        f'lambda:path/2015-03-31/functions/{lambda_function_arn}/invocations'
    try:
        # NOTE: You must specify 'POST' for integrationHttpMethod or this will not work.
        apigateway_client.put_integration(
            restApiId=api_id, resourceId=base_id, httpMethod='ANY', type='AWS_PROXY',
            integrationHttpMethod='POST', uri=lambda_uri)
        logger.info(
            "Set function %s as integration destination for the base resource.",
            lambda_function_arn)
    except ClientError:
        logger.exception(
            "Couldn't set function %s as integration destination.", lambda_function_arn)
        raise

    try:
        apigateway_client.create_deployment(restApiId=api_id, stageName=api_stage)
        logger.info("Deployed REST API %s.", api_id)
    except ClientError:
        logger.exception("Couldn't deploy REST API %s.", api_id)
        raise

    source_arn = \
        f'arn:aws:execute-api:{apigateway_client.meta.region_name}:' \
        f'{account_id}:{api_id}/*/*/{api_base_path}'
    try:
        lambda_client.add_permission(
            FunctionName=lambda_function_arn, StatementId=f'demo-invoke',
            Action='lambda:InvokeFunction', Principal='apigateway.amazonaws.com',
            SourceArn=source_arn)
        logger.info("Granted permission to let Amazon API Gateway invoke function %s "
                    "from %s.", lambda_function_arn, source_arn)
    except ClientError:
        logger.exception("Couldn't add permission to let Amazon API Gateway invoke %s.",
                         lambda_function_arn)
        raise

    return api_id


def construct_api_url(api_id, region, api_stage, api_base_path):
    """
    Constructs the URL of the REST API.

    :param api_id: The ID of the REST API.
    :param region: The AWS Region where the REST API was created.
    :param api_stage: The deployment stage of the REST API.
    :param api_base_path: The base path part of the REST API.
    :return: The full URL of the REST API.
    """
    api_url = \
        f'https://{api_id}.execute-api.{region}.amazonaws.com/' \
        f'{api_stage}/{api_base_path}'
    logger.info("Constructed REST API base URL: %s.", api_url)
    return api_url


def delete_rest_api(apigateway_client, api_id):
    """
    Deletes a REST API and all of its resources from Amazon API Gateway.

    :param apigateway_client: The Boto3 Amazon API Gateway client.
    :param api_id: The ID of the REST API.
    """
    try:
        apigateway_client.delete_rest_api(restApiId=api_id)
        logger.info("Deleted REST API %s.", api_id)
    except ClientError:
        logger.exception("Couldn't delete REST API %s.", api_id)
        raise


def usage_demo():
    """
    Shows how to deploy an AWS Lambda function, create a REST API, call the REST API
    in various ways, and remove all of the resources after the demo completes.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('-'*88)
    print("Welcome to the AWS Lambda and Amazon API Gateway REST API creation demo.")
    print('-'*88)

    lambda_filename = 'lambda_handler_rest.py'
    lambda_handler_name = 'lambda_handler_rest.lambda_handler'
    lambda_role_name = 'demo-lambda-role'
    lambda_function_name = 'demo-lambda-rest'
    api_name = 'demo-lambda-rest-api'

    iam_resource = boto3.resource('iam')
    lambda_client = boto3.client('lambda')
    apig_client = boto3.client('apigateway')

    print(f"Creating AWS Lambda function {lambda_function_name} from "
          f"{lambda_handler_name}...")
    deployment_package = lambda_basics.create_lambda_deployment_package(lambda_filename)
    iam_role = lambda_basics.create_iam_role_for_lambda(iam_resource, lambda_role_name)
    lambda_function_arn = lambda_basics.exponential_retry(
        lambda_basics.deploy_lambda_function, 'InvalidParameterValueException',
        lambda_client, lambda_function_name, lambda_handler_name, iam_role,
        deployment_package)

    print(f"Creating Amazon API Gateway REST API {api_name}...")
    account_id = boto3.client('sts').get_caller_identity()['Account']
    api_base_path = 'demoapi'
    api_stage = 'test'
    api_id = create_rest_api(
        apig_client, api_name, api_base_path, api_stage, account_id,
        lambda_client, lambda_function_arn)
    api_url = construct_api_url(
        api_id, apig_client.meta.region_name, api_stage, api_base_path)
    print(f"REST API created, URL is :\n\t{api_url}")
    print(f"Sleeping for a couple seconds to give AWS time to prepare...")
    time.sleep(2)

    print(f"Sending some requests to {api_url}...")
    https_response = requests.get(api_url)
    print(f"REST API returned status {https_response.status_code}\n"
          f"Message: {json.loads(https_response.text)['message']}")

    https_response = requests.get(
        api_url,
        params={'name': 'Martha'},
        headers={'day': calendar.day_name[datetime.date.today().weekday()]})
    print(f"REST API returned status {https_response.status_code}\n"
          f"Message: {json.loads(https_response.text)['message']}")

    https_response = requests.post(
        api_url,
        params={'name': 'Martha'},
        headers={'day': calendar.day_name[datetime.date.today().weekday()]},
        json={'adjective': 'fabulous'}
    )
    print(f"REST API returned status {https_response.status_code}\n"
          f"Message: {json.loads(https_response.text)['message']}")

    https_response = requests.delete(api_url, params={'name': 'Martha'})
    print(f"REST API returned status {https_response.status_code}\n"
          f"Message: {json.loads(https_response.text)['message']}")

    print("Deleting the REST API, AWS Lambda function, and security role...")
    time.sleep(5)  # Short sleep avoids TooManyRequestsException.
    lambda_basics.delete_lambda_function(lambda_client, lambda_function_name)
    for pol in iam_role.attached_policies.all():
        pol.detach_role(RoleName=iam_role.name)
    iam_role.delete()
    print(f"Deleted role {iam_role.name}.")
    delete_rest_api(apig_client, api_id)
    print("Thanks for watching!")


if __name__ == '__main__':
    usage_demo()
