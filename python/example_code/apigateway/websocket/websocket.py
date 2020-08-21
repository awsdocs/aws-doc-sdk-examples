# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[websocket.py demonstrates how to create a WebSocket API using API Gateway.]
# snippet-service:[apigateway]
# snippet-keyword:[API Gateway]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-07-29]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


# API Gateway Example WebSocket Chat Program
# ==========================================
#
# Program Files:
#   websocket.py             Main program source file
#   lambda_util.py           Utility functions to manage AWS Lambda functions
#   websocket_connect.py     AWS Lambda function to implement the WebSocket $connect route
#   websocket_disconnect.py  AWS Lambda function to implement the WebSocket $disconnect route
#   websocket_send_msg.py    AWS Lambda function to implement the WebSocket sendmsg custom route (Python)
#   websocket_send_msg.js    AWS Lambda function to implement the WebSocket sendmsg custom route (JavaScript)
#
# The program creates the following AWS infrastructure resources:
#       -- API Gateway WebSocket API
#       -- AWS Lambda functions for the WebSocket $connect and $disconnect routes and
#          a WebSocket sendmsg custom route
#       -- AWS Identity and Access Management (IAM) role and policy for the
#          AWS Lambda functions
#       -- Amazon DynamoDB table to store connection IDs and user names
#
# To create the WebSocket infrastructure:
#       python websocket.py
#
# To delete the WebSocket infrastructure:
#       python websocket.py -d
#       OR
#       python websocket.py --delete
#
# To use the WebSocket Chat Program:
#   1. Download and install Node.js from https://nodejs.org
#      Node.js includes the npm package manager.
#
#   2. Use npm to globally install wscat:
#           npm install -g wscat
#
#   3. When the WebSocket infrastructure is created, the WebSocket WSS address is output.
#      Copy the output WebSocket WSS address and enter it on the wscat command line to open
#      a WebSocket connection. Open multiple connections by running wscat in separate
#      terminal windows.
#           wscat -c WSS_ADDRESS
#
#      Example command line:
#           wscat -c wss://123abc456def.execute-api.us-west-2.amazonaws.com/dev
#
#      Optional: Specify a user name as a query parameter in the WSS address (Supported
#      only in the Python version of websocket_send_msg.py)
#           wscat -c wss://123abc456def.execute-api.us-west-2.amazonaws.com/dev?name=Steven
#
#   4. Send a chat message to all open connections:
#           {"action": "sendmsg", "msg": "Enter message text here"}
#
#      The "action": "sendmsg" pair invokes the WebSocket sendmsg custom route.
#      The "msg": "Enter message text here" pair specifies the message text to send.
#
#   5. To close the WebSocket connection:
#           <Ctrl-C>


import argparse
import json
import logging
import boto3
from botocore.exceptions import ClientError
from lambda_util import create_lambda_function, delete_iam_role


# Global configuration constants. Change as desired
REGION = 'us-west-2'
API_NAME = 'ExampleWebSocketAPI'
CHAT_TABLE_NAME = 'ExampleChat'

WEBSOCKET_CONNECT_NAME = 'WebSocketConnect'
WEBSOCKET_CONNECT_SRCFILE = 'websocket_connect.py'
WEBSOCKET_DISCONNECT_NAME = 'WebSocketDisconnect'
WEBSOCKET_DISCONNECT_SRCFILE = 'websocket_disconnect.py'

# At the time of this writing (2019 July), the AWS Lambda service uses a
# version of the Boto3 package (v1.9.42) that does not support the
# ApiGatewayManagementApi service. The service is needed by the WebSocket
# sendmsg custom route to POST data to open connections. As a workaround,
# a JavaScript version of the sendmsg route is provided. When the Boto3
# package used by Lambda is upgraded, the Python version of the sendmsg
# route can be used.
WEBSOCKET_SENDMSG_NAME = 'WebSocketSendMessage'
# WEBSOCKET_SENDMSG_SRCFILE = 'websocket_send_msg.py'
WEBSOCKET_SENDMSG_SRCFILE = 'websocket_send_msg.js'

LAMBDA_HANDLER_NAME = 'lambda_handler'
LAMBDA_ROLE_NAME = 'websocket-lambda-role'
WEBSOCKET_LAMBDA_POLICY = 'websocket-lambda-policy'


def create_chat_table(table_name, region):
    """Create a DynamoDB table to store WebSocket connection IDs

    :param table_name: Name of table to create
    :param region: Region for generated table
    :return: True is table was created, else False
    """

    # Define the table's attributes, key schema, and provisioned throughput
    attributes = [
        {
            'AttributeName': 'connectionId',
            'AttributeType': 'S'
        },
    ]
    key_schema = [
        {
            'AttributeName': 'connectionId',
            'KeyType': 'HASH'
        },
    ]
    provisioned_thruput = {
        'ReadCapacityUnits': 5,
        'WriteCapacityUnits': 5
    }

    # Create the DynamoDB table
    dynamodb_resource = boto3.resource('dynamodb', region_name=region)
    try:
        dynamodb_resource.create_table(TableName=table_name,
                                       AttributeDefinitions=attributes,
                                       KeySchema=key_schema,
                                       ProvisionedThroughput=provisioned_thruput)
    except ClientError as e:
        logging.error(e)
        return False

    # Optional: Wait for table to become active
    # Note: A simple table like this becomes active nearly immediately
    # table = dynamodb_resource.Table(table_name)
    # table.wait_until_exists()
    return True


def delete_chat_table(table_name, region):
    """Delete the DynamoDB table used to store WebSocket connection IDs

    :param table_name: Name of table to delete
    :param region: Region containing DynamoDB table
    :return: True if table was deleted, else False
    """

    # Delete the DynamoDB table
    dynamodb_client = boto3.client('dynamodb', region_name=region)
    try:
        dynamodb_client.delete_table(TableName=table_name)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def attach_role_policy(role_name, policy_name, api_arn, region):
    """Attach an appropriate IAM policy to the specified Lambda IAM role

    It's possible the policy was already created and attached when creating an
    earlier integration.

    If the policy is not already attached to the role, it is assumed the policy
    does not exist and it is created.

    :param role_name: String name of Lambda IAM role
    :param policy_name: String name of the IAM policy to attach to the role
    :param api_arn: String of API Gateway WebSocket API Resource ARN
    :param region: Region containing WebSocket resources
    :return: ARN of attached policy. If error, return None.
    """

    # Has the required IAM policy been attached to the role in an earlier
    # integration?
    policy_arn = get_role_policy_arn(role_name, policy_name)
    if policy_arn is not None:
        return policy_arn

    # Define an IAM policy to grant the permissions that the WebSocket
    # Lambda functions will require
    dynamodb_resource = boto3.resource('dynamodb', region_name=region)
    table = dynamodb_resource.Table(CHAT_TABLE_NAME)
    websocket_policy = {
        'Version': '2012-10-17',
        'Statement': [
            {
                'Effect': 'Allow',
                'Action': [
                    'dynamodb:DeleteItem',
                    'dynamodb:GetItem',
                    'dynamodb:PutItem',
                    'dynamodb:Scan',
                ],
                'Resource': table.table_arn,
            },
            {
                'Effect': 'Allow',
                'Action': [
                    'execute-api:ManageConnections',
                ],
                'Resource': api_arn,
            },
        ]
    }

    # Create the policy
    iam_client = boto3.client('iam')
    try:
        response = iam_client.create_policy(PolicyName=policy_name,
                                            PolicyDocument=json.dumps(websocket_policy))
    except ClientError as e:
        logging.error(e)
        return None
    policy_arn = response['Policy']['Arn']

    # Attach the policy to the Lambda role
    try:
        iam_client.attach_role_policy(RoleName=role_name,
                                      PolicyArn=policy_arn)
    except ClientError as e:
        logging.error(e)
        return None
    return policy_arn


def create_route_and_integration(api_id, route_name,
                                 lambda_function_name, lambda_srcfile,
                                 region):
    """Create a WebSocket route and an associated Lambda integration

    :param api_id: API Gateway WebSocket ID
    :param route_name: WebSocket route name, e.g., '$connect'
    :param lambda_function_name: Name of Lambda resource, e.g, 'WebSocketConnect'
    :param lambda_srcfile: Lambda source filename
    :param region: Region for generated resources
    :return: Route ID. If error, returns None.
    """

    # Define environment variables for the Lambda function
    env_vars = {
        'Variables': {
            'TableName': CHAT_TABLE_NAME,
        }
    }

    # Create the route's Lambda function
    lambda_arn = create_lambda_function(lambda_function_name,
                                        lambda_srcfile,
                                        LAMBDA_HANDLER_NAME,
                                        LAMBDA_ROLE_NAME,
                                        region,
                                        env_vars)
    if lambda_arn is None:
        return None

    # Extract the account ID from the Lambda ARN
    # ARN format="arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME"
    sections = lambda_arn.split(':')
    account_id = sections[4]

    # Grant invoke permissions on the Lambda function so it can be called by
    # API Gateway.
    # Note: To retrieve the Lambda function's permissions, call
    # LambdaClient.get_policy()
    api_arn = f'arn:aws:execute-api:{region}:{account_id}:{api_id}/*'
    source_arn = f'{api_arn}/{route_name}'
    lambda_client = boto3.client('lambda', region_name=region)
    try:
        lambda_client.add_permission(FunctionName=lambda_function_name,
                                     StatementId=f'{lambda_function_name}-invoke',
                                     Action='lambda:InvokeFunction',
                                     Principal='apigateway.amazonaws.com',
                                     SourceArn=source_arn)
    except ClientError as e:
        logging.error(e)
        return None

    # Attach appropriate policy to Lambda role
    policy_arn = attach_role_policy(LAMBDA_ROLE_NAME, WEBSOCKET_LAMBDA_POLICY,
                                    api_arn, region)
    if policy_arn is None:
        return None

    # Create the route's Lambda integration
    integration_uri = f'arn:aws:apigateway:{region}:lambda:path/2015-03-31/functions/{lambda_arn}/invocations'
    api_client = boto3.client('apigatewayv2', region_name=region)
    try:
        response = api_client.create_integration(ApiId=api_id,
                                                 IntegrationType='AWS_PROXY',
                                                 IntegrationMethod='POST',
                                                 IntegrationUri=integration_uri)
    except ClientError as e:
        logging.error(e)
        return None
    integration_id = response['IntegrationId']

    # Create the route
    target = f'integrations/{integration_id}'
    try:
        response = api_client.create_route(ApiId=api_id,
                                           RouteKey=route_name,
                                           Target=target)
    except ClientError as e:
        logging.error(e)
        return None
    return response['RouteId']


def get_role_policy_arn(role_name, policy_name):
    """Retrieve the policy ARN for a policy attached to a role

    :param role_name: String name of IAM role
    :param policy_name: String name of IAM policy attached to role_name
    :return Policy ARN. If policy not found or error, return None.
    """

    # Retrieve a batch of policies attached to the role
    iam_client = boto3.client('iam')
    try:
        response = iam_client.list_attached_role_policies(RoleName=role_name)
    except ClientError as e:
        logging.error(e)
        return None

    # Search for the desired policy
    while True:
        for policy in response['AttachedPolicies']:
            if policy['PolicyName'] == policy_name:
                # Found the desired policy
                return policy['PolicyArn']

        # Is there another batch of policies?
        if response['IsTruncated']:
            # Get another batch
            try:
                response = iam_client.list_attached_role_policies(Marker=response['Marker'])
            except ClientError as e:
                logging.error(e)
                return None
        else:
            # Policy not found
            logging.error(f'Policy {policy_name} not found attached to '
                          f'role {role_name}')
            return None


def delete_lambda_functions(region):
    """Delete the WebSocket Lambda functions and associated IAM role and policy

    :param region: Region containing WebSocket Lambda functions
    """

    # Delete the WebSocket Lambda functions
    lambda_client = boto3.client('lambda', region_name=region)
    try:
        lambda_client.delete_function(FunctionName=WEBSOCKET_CONNECT_NAME)
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Deleted Lambda $connect function: {WEBSOCKET_CONNECT_NAME}')
    try:
        lambda_client.delete_function(FunctionName=WEBSOCKET_DISCONNECT_NAME)
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Deleted Lambda $disconnect function: {WEBSOCKET_DISCONNECT_NAME}')
    try:
        lambda_client.delete_function(FunctionName=WEBSOCKET_SENDMSG_NAME)
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Deleted Lambda sendmsg function: {WEBSOCKET_SENDMSG_NAME}')

    # Delete the IAM policy attached to the Lambda IAM role
    iam_client = boto3.client('iam')
    policy_arn = get_role_policy_arn(LAMBDA_ROLE_NAME, WEBSOCKET_LAMBDA_POLICY)
    if policy_arn is not None:
        try:
            # Detach the policy from the role
            iam_client.detach_role_policy(RoleName=LAMBDA_ROLE_NAME,
                                          PolicyArn=policy_arn)
            # Delete the policy
            iam_client.delete_policy(PolicyArn=policy_arn)
        except ClientError as e:
            logging.error(e)
        else:
            logging.info(f'Deleted IAM policy: {WEBSOCKET_LAMBDA_POLICY}')

    # Detach all policies from the IAM role and delete the role
    delete_iam_role(LAMBDA_ROLE_NAME)


def create_websocket_api(api_name, region):
    """Create an API Gateway WebSocket API

    The generated API defines a route selection expression of $request.body.action
    and a stage called dev.

    :param api_name: String name of API
    :param region: Region for WebSocket resources
    :return: URI endpoint of WebSocket API. If error, return None.
    """

    # WebSocket configuration settings
    selection_expression = '$request.body.action'
    stage_name = 'dev'

    # Create the initial API infrastructure
    api_client = boto3.client('apigatewayv2', region_name=region)
    try:
        response = api_client.create_api(Name=api_name,
                                         ProtocolType='WEBSOCKET',
                                         RouteSelectionExpression=selection_expression)
    except ClientError as e:
        logging.error(e)
        return None
    api_id = response['ApiId']
    api_endpoint = response['ApiEndpoint']

    # Create the $connect route
    connect_route_id = create_route_and_integration(api_id, '$connect',
                                                    WEBSOCKET_CONNECT_NAME,
                                                    WEBSOCKET_CONNECT_SRCFILE,
                                                    region)
    if connect_route_id is None:
        return None

    # Create the $disconnect route
    disconnect_route_id = create_route_and_integration(api_id, '$disconnect',
                                                       WEBSOCKET_DISCONNECT_NAME,
                                                       WEBSOCKET_DISCONNECT_SRCFILE,
                                                       region)
    if disconnect_route_id is None:
        return None

    # Create the sendmsg route
    sendmsg_route_id = create_route_and_integration(api_id, 'sendmsg',
                                                    WEBSOCKET_SENDMSG_NAME,
                                                    WEBSOCKET_SENDMSG_SRCFILE,
                                                    region)
    if sendmsg_route_id is None:
        return None

    # Deploy the API
    try:
        response = api_client.create_deployment(ApiId=api_id)
    except ClientError as e:
        logging.error(e)
        return None
    deployment_id = response['DeploymentId']
    if response['DeploymentStatus'] == 'FAILED':
        logging.error('WebSocket deployment failed')
        return None

    # Create a stage for the deployment
    try:
        api_client.create_stage(ApiId=api_id,
                                DeploymentId=deployment_id,
                                StageName=stage_name)
    except ClientError as e:
        logging.error(e)
        return None

    # Return the API endpoint, including the stage name
    return f'{api_endpoint}/{stage_name}'


def get_websocket_api_id(api_name, region):
    """Retrieve the ID of an API Gateway WebSocket API

    :param api_name: Name of API Gateway WebSocket API
    :param region: Region containing WebSocket API
    :return: Retrieved API ID. If API not found or error, returns None.
    """

    # Retrieve a batch of APIs
    api_client = boto3.client('apigatewayv2', region_name=region)
    try:
        apis = api_client.get_apis()
    except ClientError as e:
        logging.error(e)
        return None

    # Search the batch
    while True:
        for api in apis['Items']:
            if api['Name'] == api_name:
                # Found the API we're searching for
                return api['ApiId']

        # Is there another batch of APIs?
        if 'NextToken' in apis:
            # Get another batch
            try:
                apis = api_client.get_apis(NextToken=apis['NextToken'])
            except ClientError as e:
                logging.error(e)
                return None
        else:
            # API not found
            logging.error(f'API {api_name} was not found.')
            return None


def delete_websocket_api(api_name, region):
    """Delete a WebSocket API

    :param api_name: String name of WebSocket API
    :param region: Region containing WebSocket API resources
    :return: True if API was deleted, else False
    """

    # Retrieve the ID of the WebSocket API
    api_id = get_websocket_api_id(api_name, region)
    if api_id is None:
        return False

    # Delete the WebSocket API
    api_client = boto3.client('apigatewayv2', region_name=region)
    try:
        api_client.delete_api(ApiId=api_id)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def delete_websocket_resources(region):
    """Delete all AWS resources allocated for the WebSocket example

    :param region: Region containing WebSocket resources
    """

    # Delete the WebSocket API and associated resources
    if delete_websocket_api(API_NAME, region):
        logging.info(f'Deleted WebSocketAPI: {API_NAME}')
    if delete_chat_table(CHAT_TABLE_NAME, region):
        logging.info(f'Deleted Chat table: {CHAT_TABLE_NAME}')
    delete_lambda_functions(region)


def main():
    """Exercise the WebSocket infrastructure methods"""

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Process command-line arguments
    arg_parser = argparse.ArgumentParser(description='WebSocket Example')
    arg_parser.add_argument('-d', '--delete', action='store_true',
                            help='delete allocated resources')
    args = arg_parser.parse_args()
    delete_resources = args.delete

    # Delete the allocated WebSocket resources?
    if delete_resources:
        delete_websocket_resources(REGION)
        exit(0)

    # Create a DynamoDB table to store WebSocket connection IDs
    if not create_chat_table(CHAT_TABLE_NAME, REGION):
        exit(1)

    # Create the WebSocket API
    api_endpoint = create_websocket_api(API_NAME, REGION)
    if api_endpoint is None:
        exit(1)
    logging.info(f'Created WebSocket API at {api_endpoint}')


if __name__ == '__main__':
    main()
