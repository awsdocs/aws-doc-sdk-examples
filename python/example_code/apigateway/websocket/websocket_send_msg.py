# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[websocket_send_msg.py implements a WebSocket customRoute AWS Lambda function.]
# snippet-service:[lambda]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-07-11]
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


import json
import logging
import os
import boto3
from botocore.exceptions import ClientError

# Set up logging
logging.basicConfig(format='%(levelname)s: %(asctime)s: %(message)s')
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):
    """Example WebSocket customRoute Lambda function

    The function is called when the route selection expression
    $request.body.action matches "sendmsg", as in:

        {"action": "sendmsg", "msg": "Hello"}

    :param event: Dict (usually) of parameters passed to the function
    :param context: LambdaContext object of runtime data
    :return: Dict of key:value pairs
    """

    # Log the values received in the event and context arguments
    logger.info('message event: ' + json.dumps(event, indent=2))
    connectionId = event['requestContext']['connectionId']
    logger.info(f'message event["requestContext"]["connectionId"]: {connectionId}')

    # Retrieve the name of the DynamoDB table to store connection IDs
    table_name = os.environ['TableName']

    # Retrieve the user name for the connection that's sending the message
    user_name = 'Anon'
    dynamodb_client = boto3.client('dynamodb')
    key = {'connectionId': {'S': connectionId}}
    try:
        response = dynamodb_client.get_item(TableName=table_name,
                                            Key=key,
                                            ProjectionExpression='userName')
    except ClientError as e:
        logger.error(e)
        # raise ValueError(e)
    else:
        user_name = response['Item']['userName']['S']

    # Retrieve all connection IDs from the table
    try:
        response = dynamodb_client.scan(TableName=table_name,
                                        ProjectionExpression='connectionId')
    except ClientError as e:
        logger.error(e)
        raise ValueError(e)

    # Construct the message text as bytes
    body = json.loads(event['body'])
    message = f'{user_name}: {body["msg"]}'.encode('utf-8')
    logger.info(f'Message: "{message}"')

    # Send the message to each connection
    api_client = boto3.client('apigatewaymanagementapi')
    for item in response['Items']:
        connectionId = item['connectionId']['S']
        try:
            api_client.post_to_connection(Data=message,
                                          ConnectionId=connectionId)
        except ClientError as e:
            logger.error(e)

    # Construct response
    response = {'statusCode': 200}
    return response
