#snippet-sourceauthor: [Doug-AWS]

#snippet-sourcedescription:[Description]

#snippet-service:[AWSService]

#snippet-sourcetype:[full example]

#snippet-sourcedate:[N/A]

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

# Demonstrates how to:
# 1. Enable an action for an Amazon CloudWatch alarm.
# 2. Disable all actions for an alarm.

require 'aws-sdk-cloudwatch'  # v2: require 'aws-sdk'

# Uncomment for Windows.
# Aws.use_bundled_cert!

cw = Aws::CloudWatch::Client.new(region: 'us-east-1')

# Enable an action for an Amazon CloudWatch alarm.
# If the alarm does not exist, create it.
# If the alarm exists, update its settings.
alarm_name = "TooManyObjectsInBucket"

cw.put_metric_alarm({
  alarm_name: alarm_name, 
  alarm_description: "Alarm whenever an average of more than one object exists in the specified Amazon S3 bucket for more than one day.",
  actions_enabled: true, # Run actions if the alarm's state changes.
  metric_name: "NumberOfObjects",  
  alarm_actions: [ "arn:aws:sns:REGION-ID:ACCOUNT-ID:TOPIC-NAME" ], # Notify this Amazon SNS topic only if the alarm's state changes to ALARM.
  namespace: "AWS/S3",   
  statistic: "Average",  
  dimensions: [
    {
      name: "BucketName",
      value: "my-bucket"
    },
    {
      name: "StorageType",
      value: "AllStorageTypes"
    }
  ], 
  period: 86400, # Daily (24 hours * 60 minutes * 60 seconds = 86400 seconds).
  unit: "Count", 
  evaluation_periods: 1, # More than one day.
  threshold: 1, # One object. 
  comparison_operator: "GreaterThanThreshold" # More than one object.
})

# Disable all actions for the alarm.
cw.disable_alarm_actions({
  alarm_names: [ alarm_name ]
})
