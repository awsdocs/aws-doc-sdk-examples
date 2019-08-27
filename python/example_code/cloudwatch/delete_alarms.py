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
# snippet-start:[cloudwatch.python.delete_alarms.complete]

import boto3


# Create CloudWatch client
cloudwatch = boto3.client('cloudwatch')

# Delete alarm
cloudwatch.delete_alarms(
  AlarmNames=['Web_Server_CPU_Utilization'],
)
 
 
# snippet-end:[cloudwatch.python.delete_alarms.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_alarm.py demonstrates how to delete an array of Amazon CloudWatch alarms given the alarm names.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Cloudwatch]
# snippet-service:[cloudwatch]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-12-26]
# snippet-sourceauthor:[jschwarzwalder (AWS)]

