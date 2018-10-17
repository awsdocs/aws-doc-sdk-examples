# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import boto3

# Create SQS client
sqs = boto3.client('sqs')

# Get URL for SQS queue
response = sqs.get_queue_url(QueueName='SQS_QUEUE_NAME')

print(response['QueueUrl'])
 

#snippet-sourcedescription:[get_queue_url.py demonstrates how to demonstrates how to return the URL of an existing Amazon SQS queue.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Simple Queue Service]
#snippet-service:[sqs]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-08-01]
#snippet-sourceauthor:[jschwarzwalder]

