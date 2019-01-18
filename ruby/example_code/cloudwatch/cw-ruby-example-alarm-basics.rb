# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates a CloudWatch alarm, lists your CloudWatch alarms, and deletes the CloudWatch alarm.]
# snippet-keyword:[Amazon CloudWatch]
# snippet-keyword:[delete_alarms method]
# snippet-keyword:[describe_alarms method]
# snippet-keyword:[put_metric_alarm method]
# snippet-keyword:[Ruby]
# snippet-service:[cloudwatch]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
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

# Demonstrates how to:
# 1. Get a list of your Amazon CloudWatch alarms.
# 2. Create an alarm.
# 3. Delete the alarm.

require 'aws-sdk-cloudwatch'  # v2: require 'aws-sdk'

# Uncomment for Windows.
# Aws.use_bundled_cert!

cw = Aws::CloudWatch::Client.new(region: 'us-east-1')

# Get a list of your Amazon CloudWatch alarms.
describe_alarms_output = cw.describe_alarms()

describe_alarms_output.metric_alarms.each do |alarm|
  puts alarm.alarm_name
end 

# Create an alarm.
# In this example, alarm whenever an average of more than one object exists in the specified Amazon S3 bucket for more than one day.
# For a list of available metric names, namespaces, statistics, extended statistics, dimensions, and units, see the
# "Metrics and Dimensions Reference" section in the Amazon CloudWatch User Guide. 
alarm_name = "TooManyObjectsInBucket"

cw.put_metric_alarm({
  alarm_name: alarm_name, 
  alarm_description: "Alarm whenever an average of more than one object exists in the specified Amazon S3 bucket for more than one day.",
  actions_enabled: false, # Do not take any actions if the alarm's state changes.
  metric_name: "NumberOfObjects",  
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
  comparison_operator: "GreaterThanThreshold"
})

# Delete the alarm.
cw.delete_alarms({
  alarm_names: [ alarm_name ]
})
