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
# snippet-start:[sns.python.list_subscriptions.complete]

import boto3

# Create an SNS client
sns = boto3.client('sns')

# Call SNS to list the first 100 subscriptions
response = sns.list_subscriptions()

# Get a list of subscriptions from the response
subscriptions = []
for subscription in response['Subscriptions']:
    subscriptions.append(subscription)

# Print out the subscriptions list
print("Subscription List: %s" % subscriptions)
 
 
# snippet-end:[sns.python.list_subscriptions.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[sns-python-example-list-subscriptions.py demonstrates how to retrieve a list of AWS SNS subscriptions.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-service:[sns]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-12-26]
# snippet-sourceauthor:[jasonhedges]