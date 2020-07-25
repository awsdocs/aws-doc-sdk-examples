# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that handles calls from an Amazon API
Gateway REST API.
"""

import json
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):
    """
    Handles requests that are passed through an Amazon API Gateway REST API.
    GET, POST, and PUT requests all result in success codes that echo back input
    parameters in a message. DELETE requests result in a 405 response.

    Several kinds of REST API parameters are demonstrated:
    * Query string: 'name' can be sent in the query string,
      for example: demoapi?name=Martha
    * Custom header: 'day' can be sent as a custom header, for example: 'day: Thursday'
    * Body: 'adjective' can be sent in the request body, encoded as JSON,
      for example: {"adjective": "fantastic"}

    :param event: The event dict sent by Amazon API Gateway that contains all of the
                  request data.
    :param context: The context in which the function is called.
    :return: A response that is sent to Amazon API Gateway, to be wrapped into
             an HTTP response. The 'statusCode' field is the HTTP status code
             and the 'body' field is the body of the response.
    """
    logger.info("Request: %s", event)
    response_code = 200

    http_method = event.get('httpMethod')
    query_string = event.get('queryStringParameters')
    headers = event.get('headers')
    body = event.get('body')

    name = 'D. E. Fault'
    if query_string is not None:
        name = query_string.get('name', name)
    day = 'None day'
    if headers is not None:
        day = headers.get('day', day)
    adjective = 'nice'
    if body is not None:
        adjective = json.loads(body).get('adjective', adjective)

    if http_method == 'GET':
        greeting = f"Got your GET, {name}."
    elif http_method == 'POST':
        greeting = f"Nice POST, {name}."
    elif http_method == 'PUT':
        greeting = f"I'll just PUT this here for you, {name}."
    else:
        greeting = f"Sorry, {name}, {http_method} isn't allowed."
        response_code = 405

    greeting += f" Have a {adjective} {day}!"

    response = {
        'statusCode': response_code,
        'body': json.dumps({'message': greeting, 'input': event})
    }

    logger.info("Response: %s", response)
    return response
