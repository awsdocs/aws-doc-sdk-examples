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
# snippet-start:[sns.python.publish_to_topic.complete]

import boto3

# Create an SNS client
sns = boto3.client('sns')

# Publish a simple message to the specified SNS topic
response = sns.publish(
    TopicArn='arn:aws:sns:region:0123456789:my-topic-arn',    
    Message='Hello World!',    
)

# Print out the response
print(response)
  
#snippet-end:[sns.python.publish_to_topic.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[sns-python-example-publich-to-topic.py demonstrates how to publish a message to an AWS SNS topic.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Simple Notification Service]
#snippet-service:[sns]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[jasonhedges]