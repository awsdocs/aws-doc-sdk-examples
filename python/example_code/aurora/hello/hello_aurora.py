# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.rds.HelloRDSAurora]
import boto3

# Create an RDS client
rds = boto3.client('rds')

# Call the describe_db_clusters operation to list the clusters in your account
response = rds.describe_db_clusters()

# Check if any clusters are returned and print the appropriate message
if 'DBClusters' in response and response['DBClusters']:
    print("Here are your RDS Aurora clusters:")
    for cluster in response['DBClusters']:
        print(f"Cluster ID: {cluster['DBClusterIdentifier']}, Engine: {cluster['Engine']}")
else:
    print("No clusters found!")

# If the response is truncated, handle pagination
while 'Marker' in response:
    marker = response['Marker']
    response = rds.describe_db_clusters(Marker=marker)
    if 'DBClusters' in response and response['DBClusters']:
        for cluster in response['DBClusters']:
            print(f"Cluster ID: {cluster['DBClusterIdentifier']}, Engine: {cluster['Engine']}")
    else:
        print("No more clusters found!")

# snippet-end:[python.example_code.rds.HelloRDSAurora]
