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

# snippet-sourcedescription:[add_tags_to_cluster_and_instances.py demonstrates how to propagate same tags to a cluster and all of its instances.]
# snippet-service:[RDS]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Relational Database Service]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-10-01]
# snippet-sourceauthor:[AWS]
# snippet-start:[rds.python.add_tags_to_cluster_and_instances.complete]

import boto3

# Defined constants
# Change to the appropriate region of your cluster
regionName = 'us-east-1'
# Specify the cluster identifier
clusterIdentifier = 'cluster-name'
# Specify your account number
accountNumber = '123456789012'
#Specify the list of tags that will be applied to cluster and the instances in it
tags = [{'Key':'tag1', 'Value':'value1'}, {'Key':'tag2', 'Value':'value2'}]

def fetch_cluster_instances():
	"""Fetches all of the instance Ids that are in a given cluster"""

	rds = boto3.client('rds', region_name = regionName)
	try:
		print("Fetching cluster information for cluster ", clusterIdentifier)
		result = rds.describe_db_clusters(DBClusterIdentifier = clusterIdentifier)
		cluster = result['DBClusters'][0]
		clusterMembers = cluster['DBClusterMembers']
		instanceIdentifiers = []
		for instance in clusterMembers:
			instanceIdentifiers.append(instance['DBInstanceIdentifier'])
		return instanceIdentifiers
	except Exception as e:
		print("Error while fetching cluster data: ", e)
		raise e

def apply_tags_to_instances_and_cluster(instanceIdentifiers):
	"""Applies the specified tags to the cluster and all of the instances in the cluster

	Note: If there is a failure in the middle of the process, some of the instances might have
	the tags applied and some of them not. This method does not rollback to initial state.
	In a case of a failure it is adviseable to try and run the script again after a while or 
	manually check and revert the applied tags to the instances and the cluster.

	"""

	rds = boto3.client('rds', region_name = regionName)
	try:
		clusterARN = generate_ARN_for_resource(clusterIdentifier, True)
		rds.add_tags_to_resource(ResourceName=clusterARN,Tags=tags)
		print("Succesfully applied tags to cluster " + clusterIdentifier)
		for instanceId in instanceIdentifiers:
			instanceARN = generate_ARN_for_resource(instanceId, False)
			rds.add_tags_to_resource(ResourceName=instanceARN,Tags=tags)
			print("Succesfully applied tags to instance " + instanceId)
	except Exception as e:
		print("Error while applying tags:  ", e)
		raise e

def generate_ARN_for_resource(resourceId, isCluster):
	""" Generates the ARN that represents a cluster or instance resource """
	resourceType = ":cluster:" if isCluster else ":db:"
	return "arn:aws:rds:" + regionName + ":" + accountNumber + resourceType + resourceId
 
def main():
	""" This script fetches all of the instances of the given cluster and applies the specified tags
	to the cluster and its instances """

	print("Applying tags to cluster and all of the instances in the cluster")
	instanceIdentifiers = fetch_cluster_instances()
	apply_tags_to_instances_and_cluster(instanceIdentifiers)
	print("SUCCESS")

if __name__ == '__main__':
    main()

# snippet-end:[rds.python.add_tags_to_cluster_and_instances.complete]