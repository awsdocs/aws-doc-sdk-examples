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


def create_recovery_client(cluster_endpoint):
    """
    Creates a Boto3 Route 53 Application Recovery Controller client for the specified
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
    Gets the state of a routing control. Cluster endpoints are tried in
    sequence until the first successful response is received.

    :param routing_control_arn: The ARN of the routing control to look up.
    :param cluster_endpoints: The list of cluster endpoints to query.
    :return: The routing control state response.
    """
    for cluster_endpoint in cluster_endpoints:
        try:
            recovery_client = create_recovery_client(cluster_endpoint)
            response = recovery_client.get_routing_control_state(
                RoutingControlArn=routing_control_arn)
            return response
        except Exception as error:
            print(error)
# snippet-end:[python.example_code.route53-recovery-cluster.GetRoutingControlState]


# snippet-start:[python.example_code.route53-recovery-cluster.UpdateRoutingControlState]
def update_routing_control_state(
        routing_control_arn, cluster_endpoints, routing_control_state):
    """
    Updates the state of a routing control. Cluster endpoints are tried in
    sequence until the first successful response is received.

    :param routing_control_arn: The ARN of the routing control to update the state for.
    :param cluster_endpoints: The list of cluster endpoints to try.
    :param routing_control_state: The new routing control state.
    :return: The routing control update response.
    """
    for cluster_endpoint in cluster_endpoints:
        try:
            recovery_client = create_recovery_client(cluster_endpoint)
            response = recovery_client.update_routing_control_state(
                RoutingControlArn=routing_control_arn,
                RoutingControlState=routing_control_state)
            return response
        except Exception as error:
            print(error)
# snippet-end:[python.example_code.route53-recovery-cluster.UpdateRoutingControlState]


# snippet-start:[python.example_code.route53-recovery-cluster.Scenario_SetControlState]
def toggle_routing_control_state(routing_control_arn, cluster_endpoints):
    """
    Shows how to get and set the state of a routing control for a cluster.
    """
    response = get_routing_control_state(routing_control_arn, cluster_endpoints)
    state = response['RoutingControlState']
    print('-'*88)
    print(
        f"Starting state of control {routing_control_arn}: {state}")
    print('-'*88)

    update_state = 'Off' if state == 'On' else 'On'
    print(f"Setting control state to '{update_state}'.")
    response = update_routing_control_state(routing_control_arn, cluster_endpoints, update_state)
    if response:
        print('Success!')
    else:
        print(f'Something went wrong.')
    print('-'*88)
# snippet-end:[python.example_code.route53-recovery-cluster.Scenario_SetControlState]


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('routing_control_arn', help="The ARN of the routing control.")
    parser.add_argument(
        'cluster_endpoints', help="A JSON file containing the list of endpoints for the cluster.")
    args = parser.parse_args()
    with open(args.cluster_endpoints) as endpoints_file:
        cluster_endpoints = json.load(endpoints_file)['ClusterEndpoints']
    toggle_routing_control_state(args.routing_control_arn, cluster_endpoints)
