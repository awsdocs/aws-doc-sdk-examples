# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

import boto3
import botocore

def get_routing_control_state(routing_control_arn, cluster_endpoints):
	for cluster_endpoint in cluster_endpoints:
		try:
			client = boto3.client('route53-recovery-cluster', endpoint_url=cluster_endpoint['endpoint_url'], region_name=cluster_endpoint['region'])
			response = client.get_routing_control_state(RoutingControlArn=args.routing_control_arn)
			if response['ResponseMetadata']['HTTPStatusCode'] != 200:
				raise ClientError('Request failed with error: ' + response['ResponseMetadata'])
			return response
		except botocore.exceptions.ClientError as error:
			print(error)
			continue