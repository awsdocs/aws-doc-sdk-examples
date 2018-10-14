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

# Create a SQS queue
response = sqs.create_queue(
    QueueName='SQS_QUEUE_NAME',
    Attributes={
        'DelaySeconds': '60',
        'MessageRetentionPeriod': '86400'
    }
)

print(response['QueueUrl'])
 

#snippet-sourcedescription:[create_queue.py demonstrates how to create a new FIFO queue with Amazon SQS.]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-service:[<<ADD SERVICE>>]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]

