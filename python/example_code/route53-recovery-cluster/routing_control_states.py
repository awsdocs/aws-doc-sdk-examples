# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Route 53 Application
Recovery Controller to manage routing controls.
"""

import argparse
import json

# snippet-start:[python.example_code.route53-recovery-cluster.helper.get_recovery_client]
import boto3
from botocore.exceptions import ClientError


def create_recovery_client(cluster_endpoint):
    """
    Creates a Boto3 Route 53 Application Recovery Controller for the specified
    cluster endpoint URL and AWS Region.

    :param cluster_endpoint: The cluster endpoint URL and Region.
    :return: The Boto3 client.
    """
    return boto3.client(
        'route53-recovery-cluster',
        endpoint_url=cluster_endpoint['Endpoint'],
        region_name=cluster_endpoint['Region'])
# snippet-end:[python.example_code.route53-recovery-cluster.helper.get_recovery_client]


# snippet-start:[python.example_code.route53-recovery-cluster.GetRoutingControlState]
def get_routing_control_state(routing_control_arn, cluster_endpoints):
    """
    Gets the state of a routing control for a list of cluster endpoints.

    :param routing_control_arn: The ARN of the routing control to look up.
    :param cluster_endpoints: The list of cluster endpoints to query.
    :return: The routing control state response for each endpoint.
    """
    responses = {}
    for cluster_endpoint in cluster_endpoints:
        recovery_client = create_recovery_client(cluster_endpoint)
        try:
            response = recovery_client.get_routing_control_state(
                RoutingControlArn=routing_control_arn)
            if response['ResponseMetadata']['HTTPStatusCode'] != 200:
                raise ClientError(
                    f"Request failed with error: {response['ResponseMetadata']}",
                    'GetRoutingControlState')
            else:
                responses[cluster_endpoint['Endpoint']] = response
        except ClientError as error:
            print(error)
    return responses
# snippet-end:[python.example_code.route53-recovery-cluster.GetRoutingControlState]


# snippet-start:[python.example_code.route53-recovery-cluster.UpdateRoutingControlState]
def update_routing_control_state(
        routing_control_arn, cluster_endpoints, routing_control_state):
    """
    Updates the state of a routing control for a list of cluster endpoints.

    :param routing_control_arn: The ARN of the routing control to set.
    :param cluster_endpoints: The list of cluster endpoints to set.
    :param routing_control_state: The state to set for the routing control.
    :return: The routing control update response for each endpoint.
    """
    responses = {}
    for cluster_endpoint in cluster_endpoints:
        recovery_client = create_recovery_client(cluster_endpoint)
        try:
            response = recovery_client.update_routing_control_state(
                RoutingControlArn=routing_control_arn,
                RoutingControlState=routing_control_state)
            if response['ResponseMetadata']['HTTPStatusCode'] != 200:
                raise ClientError(
                    f"Request failed with error: {response['ResponseMetadata']}",
                    'UpdateRoutingControlState')
            else:
                responses[cluster_endpoint['Endpoint']] = response
        except ClientError as error:
            print(error)
    return responses
# snippet-end:[python.example_code.route53-recovery-cluster.UpdateRoutingControlState]


# snippet-start:[python.example_code.route53-recovery-cluster.UpdateRoutingControlStates]
def update_routing_control_states(
        update_routing_control_state_entries, cluster_endpoints):
    """
    Updates the state of a list of routing controls for a list of cluster endpoints.

    :param update_routing_control_state_entries: The list of routing controls to
                                                 update for each endpoint and the state
                                                 to set them to.
    :param cluster_endpoints: The list of cluster endpoints to set.
    :return: The routing control update response for each endpoint.
    """
    responses = {}
    for cluster_endpoint in cluster_endpoints:
        recovery_client = create_recovery_client(cluster_endpoint)
        try:
            response = recovery_client.update_routing_control_states(
                UpdateRoutingControlStateEntries=update_routing_control_state_entries)
            if response['ResponseMetadata']['HTTPStatusCode'] != 200:
                raise ClientError(
                    f"Request failed with error: {response['ResponseMetadata']}",
                    'UpdateRoutingControlStates')
            else:
                responses[cluster_endpoint['Endpoint']] = response
        except ClientError as error:
            print(error)
    return responses
# snippet-end:[python.example_code.route53-recovery-cluster.UpdateRoutingControlStates]


# snippet-start:[python.example_code.route53-recovery-cluster.Scenario_SetControlState]
def set_routing_control_state_on(routing_control_arn, cluster_endpoints):
    """
    Shows how to get and set the state of a routing control for a list of endpoints.
    """
    responses = get_routing_control_state(routing_control_arn, cluster_endpoints)
    print('-'*88)
    print(f"Starting state of control {routing_control_arn}:")
    for url, response in responses.items():
        print(f"{url}: {response['RoutingControlState']}")
    print('-'*88)

    print(f"Setting all endpoints to 'On'.")
    responses = update_routing_control_state(
        routing_control_arn, cluster_endpoints, 'On')
    for url, response in responses.items():
        print(f"{url}: {response['ResponseMetadata']['HTTPStatusCode']}")
    print('-'*88)
# snippet-end:[python.example_code.route53-recovery-cluster.Scenario_SetControlState]


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('routing_control_arn', help="The ARN of the routing control.")
    parser.add_argument(
        'cluster_endpoints', help="The list of cluster endpoints to query in JSON format.")
    args = parser.parse_args()
    set_routing_control_state_on(
        args.routing_control_arn, json.loads(args.cluster_endpoints))
