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
import sys
import json

# Sample usage
# python3 add_tags_to_cluster_and_instances.py regionName=us-east-1 clusterIdentifier=cluster-name accountNumber=123456789012 tags='[{"Key":"Tag1","Value":"Value1"},{"Key":"Tag2","Value":"Value2"}]'
# 
# Four arguments need to be supplied
# regionName=Region
# clusterIdentifier=Name_Of_Cluster
# accountNumber=Customer_account_number
# tags=Json_with_tags format [{"Key":"key","Value":"value"},...]


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
 
def applyArguments(argv):
	""" Reads the supplied arguments and applies them """
	settings = dict()
	settings["regionName"] = ""
	settings["clusterIdentifier"] = ""
	settings["accountNumber"] = ""
	settings["tags"] = ""
	if (len(settings) != len(argv) - 1):
		raise Exception("Incorrect number of argumnets supplied. Expected ", settings.keys())
	for pos,arg in enumerate(argv):
		if pos==0:
			continue
		a=arg.split('=')
		if len(a) != 2:
			raise Exception("Incorrect usage. Format argument=value ...")
		if not a[0] in settings:
			raise Exception("Argument  " + a[0] + " does not exist. Expected ", settings.keys())	
		settings[a[0]] = a[1]	
	for key, value in settings.items():
		if value == "":
			raise Exception("Missing value for argument " + key)
	global regionName 
	regionName = settings["regionName"]
	global clusterIdentifier 
	clusterIdentifier = settings["clusterIdentifier"]
	global accountNumber
	accountNumber = settings["accountNumber"]
	global tags
	try:
		tags = json.loads(settings["tags"])
	except Exception as e:
		raise Exception("Cannot parse json supplied for tags ", e)

def main(argv):
	""" This script fetches all of the instances of the given cluster and applies the specified tags
	to the cluster and its instances """
	print("Checking arguments")
	applyArguments(argv);
	print("Applying tags to cluster and all of the instances in the cluster")
	instanceIdentifiers = fetch_cluster_instances()
	apply_tags_to_instances_and_cluster(instanceIdentifiers)
	print("SUCCESS")

if __name__ == '__main__':
    main(sys.argv)

# snippet-end:[rds.python.add_tags_to_cluster_and_instances.complete]