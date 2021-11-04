# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import botocore

def update_routing_control_states(update_routing_control_state_entries, cluster_endpoints):
	for cluster_endpoint in cluster_endpoints:
		try:
			client = boto3.client('route53-recovery-cluster', endpoint_url=cluster_endpoint['endpoint_url'], region_name=cluster_endpoint['region'])
			response = client.update_routing_control_states(UpdateRoutingControlStateEntries=update_routing_control_state_entries)
			if response['ResponseMetadata']['HTTPStatusCode'] != 200:
				raise ClientError('Request failed with error: ' + response['ResponseMetadata'])
			return response
		except botocore.exceptions.ClientError as error:
			print(error)
			continue