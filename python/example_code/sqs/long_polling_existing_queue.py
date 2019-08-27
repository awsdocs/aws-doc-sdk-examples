# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[long_polling_existing_queue.py demonstrates how to set the default number of seconds to wait between retrieving a message.]
# snippet-service:[sqs]
# snippet-keyword:[Amazon Simple Queue Service]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2018-08-01]
# snippet-sourceauthor:[jschwarzwalder (AWS)]

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

# Assign this value to an existing queue URL before running the program
queue_url = 'SQS_QUEUE_URL'

# Enable long polling on the queue
sqs = boto3.client('sqs')
sqs.set_queue_attributes(QueueUrl=queue_url,
                         Attributes={'ReceiveMessageWaitTimeSeconds': '20'})
