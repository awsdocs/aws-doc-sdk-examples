# Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk'

# Placeholder for put_metric_alarm args
args = {}
args[:alarm_name] = 'my-alarm'
args[:alarm_description] = 'Triggers alarm when S3 bucket my-bucket has more than 50 items'
args[:alarm_actions] = 'ARN'
args[:namespace] = 'AWS/S3'
args[:metric_name] = 'NumberOfObjects'

dim1 = {}
dim1[:name] = 'BucketName'
dim1[:value] = 'my-bucket'

dim2 = {}
dim2[:name] = 'StorageType'
dim2[:value] = 'AllStorageTypes'

dimensions = []

dimensions << dim1
dimensions << dim2

args[:dimensions] = dimensions

args[:statistic] = 'Maximum'

# NumberOfObjects REQUIRES this value
args[:period] = 86400

# NumberOfObjects REQUIRES this value
args[:unit] = nil

args[:evaluation_periods] = 1
args[:threshold] = 50
args[:comparison_operator] = 'GreaterThanThreshold'

cw = Aws::CloudWatch::Client.new(region: 'us-west-2')

cw.put_metric_alarm(args)
