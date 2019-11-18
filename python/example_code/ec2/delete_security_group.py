# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-start:[ec2.python.delete_security_group.complete]


import boto3
from botocore.exceptions import ClientError

# Create EC2 client
ec2 = boto3.client('ec2')

# Delete security group
try:
    response = ec2.delete_security_group(GroupId='SECURITY_GROUP_ID')
    print('Security Group Deleted')
except ClientError as e:
    print(e)
 
 
# snippet-end:[ec2.python.delete_security_group.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_security_group.py demonstrates how to delete an Amazon EC2 security group.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon EC2]
# snippet-service:[ec2]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-12-26]
# snippet-sourceauthor:[jschwarzwalder (AWS)]

