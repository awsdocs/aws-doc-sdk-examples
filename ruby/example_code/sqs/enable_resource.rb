# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
# This code example demonstrates how to enable a resource to send a message to an
# Amazon Simple Queue Service (Amazon SQS) queue.

# snippet-start:[sqs.Ruby.enableResource]
require 'aws-sdk-sqs' # v2: require 'aws-sdk'
sqs = Aws::SQS::Client.new

policy = '{
  "Version":"2008-10-17",
  "Id":' + my - queue - arn + '/SQSDefaultPolicy",
  "Statement":[{
    "Sid":"__default_statement_ID",
    "Effect":"Allow",
    "Principal":{
      "AWS":"*"
    },
    "Action":["SQS:SendMessage"],
    "Resource":"' + my - queue - arn + '",
    "Condition":{
      "ArnEquals":{
        "AWS:SourceArn":"' + my - resource - arn + '"}
     }
  }]
}'

sqs.set_queue_attributes({
                           queue_url: my - queue - url,
                           attributes: {
                             Policy: policy
                           }
                         })
# snippet-end:[sqs.Ruby.enableResource]
