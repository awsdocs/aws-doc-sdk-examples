# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
# snippet-start:[ecs.python.create_cluster.complete]

import boto3

# Create ECS client
try:
  ecs_client = boto3.client('ecs')
  
  response = ecs_client.create_cluster(
    clusterName='CLUSTER_NAME'
  )
  print(response)

except BaseException as exe:
    print(exe)

#snippet-end:[ecs.python.create_cluster.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[create_cluster.py demonstrates how to create an Amazon ECS cluster.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Elastic Container Service (ECS)]
#snippet-service:[ecs]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[Evalle]
