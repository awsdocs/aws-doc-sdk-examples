# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

# Create a SQS queue with long polling enabled
response = sqs.create_queue(
    QueueName='SQS_QUEUE_NAME',
    Attributes={'ReceiveMessageWaitTimeSeconds': '20'}
)

print(response['QueueUrl'])
 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[long_polling_create_queue.py demonstrates how to create a queue with a default 20 second to wait between retrieving messages. This will reduce the number of empty responses returned and thus your bill.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Simple Queue Service]
#snippet-service:[sqs]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-08-01]
#snippet-sourceauthor:[jschwarzwalder (AWS)]

