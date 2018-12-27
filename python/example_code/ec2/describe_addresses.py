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
# snippet-start:[ec2.python.describe_addresses.complete]


import boto3

ec2 = boto3.client('ec2')
filters = [
    {'Name': 'domain', 'Values': ['vpc']}
]
response = ec2.describe_addresses(Filters=filters)
print(response)
 
 
#snippet-end:[ec2.python.describe_addresses.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[describe_addresses.py demonstrates how to describe one or more of your Elastic IP addresses. An Elastic IP address is for use in either the Amazon EC2-Classic platform or in a VPC.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon EC2]
#snippet-service:[ec2]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[jschwarzwalder (AWS)]

