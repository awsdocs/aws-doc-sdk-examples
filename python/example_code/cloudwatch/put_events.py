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
# snippet-start:[cloudwatch.python.put_events.complete]

import json

import boto3


# Create CloudWatchEvents client
cloudwatch_events = boto3.client('events')

# Put an event
response = cloudwatch_events.put_events(
    Entries=[
        {
            'Detail': json.dumps({'key1': 'value1', 'key2': 'value2'}),
            'DetailType': 'appRequestSubmitted',
            'Resources': [
                'RESOURCE_ARN',
            ],
            'Source': 'com.company.myapp'
        }
    ]
)
print(response['Entries'])
 
 
# snippet-end:[cloudwatch.python.put_events.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_events.py demonstrates how to send an event that is matched to targets for handling.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Cloudwatch]
# snippet-service:[cloudwatch]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-12-26]
# snippet-sourceauthor:[jschwarzwalder (AWS)]

