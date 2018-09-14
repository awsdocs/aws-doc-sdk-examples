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

require 'aws-sdk-s3'  # v2: require 'aws-sdk'

req = {}
req[:bucket] = bucket_name

events = ['s3:ObjectCreated:*']

notification_configuration = {}

# Add function
lc = {}

lc[:lambda_function_arn] = 'my-function-arn'
lc[:events] = events
lambda_configurations = []
lambda_configurations << lc

notification_configuration[:lambda_function_configurations] = lambda_configurations

# Add queue
qc = {}

qc[:queue_arn] = 'my-topic-arn'
qc[:events] = events
queue_configurations = []
queue_configurations << qc

notification_configuration[:queue_configurations] = queue_configurations

# Add topic
tc = {}

tc[:topic_arn] = 'my-topic-arn'
tc[:events] = events
topic_configurations = []
topic_configurations << tc

notification_configuration[:topic_configurations] = topic_configurations

req[:notification_configuration] = notification_configuration

req[:use_accelerate_endpoint] = false

s3 = Aws::S3::Client.new(region: 'us-west-2')

s3.put_bucket_notification_configuration(req)
