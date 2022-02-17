# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# sns-ruby-example-enable-resource.rb demonstrates how to enable an Amazon Simple Notification Service (SNS) resource using
# the AWS SDK for Ruby.

# Inputs:
# - MY_TOPIC_ARN
# - MY_RESOURCE_ARN
# - REGION
# - POLICY_NAME

# snippet-start:[sns.Ruby.enableResource]

require 'aws-sdk-sns'  # v2: require 'aws-sdk'

policy  = '{
  "Version":"2008-10-17",
  "Id":"__default_policy_ID",
  "Statement":[{
    "Sid":"__default_statement_ID",
    "Effect":"Allow",
    "Principal":{
      "AWS":"*"
    },
    "Action":["SNS:Publish"],
    "Resource":"' + MY_TOPIC_ARN + '",
    "Condition":{
      "ArnEquals":{
        "AWS:SourceArn":"' + MY_RESOURCE_ARN + '"}
     }
  }]
}'
# Replace us-west-2 with the AWS Region you're using for Amazon SNS.
sns = Aws::SNS::Resource.new(region: 'REGION')

# Get topic by ARN
topic = sns.topic()

# Add policy to topic
topic.set_attributes({
  attribute_name: "POLICY_NAME",
  attribute_value: policy
})

# snippet-end:[sns.Ruby.enableResource]
