# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This is a simple test server for use with the How to Build and Manage a Resilient Service
example. It simulates an engine for recommending books, movies, and songs.

This code is only for use in this example.
*** NOT FOR PRODUCTION USE ***
"""

import argparse
from http.server import BaseHTTPRequestHandler, HTTPServer
import json
from functools import partial
import random

import boto3
from botocore.exceptions import ClientError
from ec2_metadata import ec2_metadata


class RequestHandler(BaseHTTPRequestHandler):
    """Handles HTTP requests by returning a recommendation or responding to a health check."""
    def __init__(self, dynamodb_client, ssm_client, *args, **kwargs):
        """
        :param dynamodb_client: A Boto3 DynamoDB client.
        :param ssm_client: A Boto3 Systems Manager client.
        """
        self.dynamodb_client = dynamodb_client
        self.ssm_client = ssm_client
        super().__init__(*args, **kwargs)

    def _respond(self, status_code, payload):
        self.send_response(status_code)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(bytes(json.dumps(payload), "utf-8"))

    def do_GET(self):
        """
        Responds to an HTTP GET request. This function uses Systems Manager parameters
        to determine how to respond to different kinds of requests, in order to simulate
        failures or to make the server more resilient to failure.

        Root path '/':
        * Returns a recommendation by getting a random item from the recommendations table.
        * The table name is specified by a Systems Manager parameter. When this parameter
          is set to a non-existent table, either an error response is returned or a static
          response is returned.
        * This example uses the ec2_metadata package to get metadata about the instance
          where the server is running and include metadata in the response.

        Health check path '/healthcheck':
        * When shallow checks are specified, always returns success.
        * When deep checks are specified, attempts to get data about the DynamoDB table and,
          if the connection attempt fails, returns an error response.
        """
        print("path: ", self.path)

        table = 'doc-example-resilient-architecture-table'
        failure_response = 'doc-example-resilient-architecture-failure-response'
        health_check = 'doc-example-resilient-architecture-health-check'
        param_response = self.ssm_client.get_parameters(Names=[table, failure_response, health_check])
        parameters = {p['Name']: p['Value'] for p in param_response['Parameters']}
        print(parameters)

        if self.path == '/':
            try:
                media_type = random.choice(['Book', 'Movie', 'Song'])
                item_id = random.randint(1, 3)
                response = self.dynamodb_client.get_item(
                    TableName=parameters[table],
                    Key={'MediaType': {'S': media_type}, 'ItemId': {'N': str(item_id)}})
                payload = response['Item']
            except ClientError as err:
                print(f"Recommendation service error: {err}")
                if parameters[failure_response] == 'static':
                    payload = {
                        "MediaType": {"S": "Book"},
                        "ItemId":{"N": "0"},
                        "Title": {"S": "404 Not Found: A Coloring Book"},
                        "Creator": {"S": "The Oatmeal"}}
                else:
                    raise err

            payload['Metadata'] = {
                'InstanceId': ec2_metadata.instance_id,
                'AvailabilityZone': ec2_metadata.availability_zone}
            self._respond(200, payload)
        elif self.path == '/healthcheck':
            response_code = 200
            success = True
            if not health_check in parameters:
                print(f"{health_check} parameter not found.")
            elif parameters[health_check] == 'deep':
                try:
                    response = self.dynamodb_client.describe_table(TableName=parameters[table])
                    if response['Table']['TableStatus'] == 'ACTIVE':
                        response_code = 200
                        success = True
                    else:
                        response_code = 503
                        success = False
                except ClientError as err:
                    print(f"Recommendation service health check error: {err}")
                    response_code = 503
                    success = False
            self._respond(response_code, {'success': success})


def run():
    """
    Runs a web server that listens for HTTP requests on the specified port.
    To simplify the example, the web server is run as the root user and uses a simple
    Python web server that is intended only for development and testing.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument('port', default=80, type=int, help="The port where the HTTP server listens.")
    parser.add_argument('--region', default=ec2_metadata.region, help="The AWS Region of AWS resources used by this example.")
    args = parser.parse_args()

    server_port = args.port
    server_ip = '0.0.0.0'

    print('Starting server...')
    server_address = (server_ip, server_port)

    dynamodb_client = boto3.client('dynamodb', region_name=args.region)
    ssm_client = boto3.client('ssm', region_name=args.region)
    handler = partial(RequestHandler, dynamodb_client, ssm_client)
    httpd = HTTPServer(server_address, handler)
    print('Running server...')
    httpd.serve_forever()


if __name__ == "__main__":
    run()
