# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates a rule in CloudWatch events and adds a target for the rule.]
# snippet-keyword:[Amazon CloudWatch]
# snippet-keyword:[put_rule method]
# snippet-keyword:[put_target method]
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
# 1. Create a rule in Amazon CloudWatch Events.
# 2. Add a target to the rule.
# 3. Send an event to Amazon CloudWatch Events so that it can be matched to the rule.
# 4. View the results in Amazon CloudWatch Metrics and Logs.

# To test this code, you must have:
# 1. An AWS Lambda function that uses the hello-world blueprint to serve as the target for events. To learn how,
#    see "Step 1: Create a Lambda Function" of the "Tutorial: Log the State of an EC2 Intance Using CloudWatch Events"
#    topic in the Amazon CloudWatch Events User Guide.
#    Make a note of the AWS Lambda function Amazon Resource Name (ARN), as you will need it later in the code.
# 2. An AWS IAM service role containing a policy granting permission to Amazon CloudWatch Events.
#    For the role policy, use:
=begin

{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "CloudWatchEventsFullAccess",
      "Effect": "Allow",
      "Resource": "*",
      "Action": "events:*"
    },
    {
      "Sid": "IAMPassRoleForCloudWatchEvents",
      "Effect": "Allow",
      "Resource": "arn:aws:iam::*:role/AWS_Events_Invoke_Targets",
      "Action": "iam:PassRole"
    }
  ]
}

=end

#    For the trust relationship, use:
=begin

{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "events.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}

=end
#    Make a note of the AWS IAM service role ARN, as you will need it later in the code.

require 'aws-sdk-cloudwatch'  # v2: require 'aws-sdk'

# Uncomment for Windows.
# Aws.use_bundled_cert!

cwe = Aws::CloudWatchEvents::Client.new(region: 'us-east-1')

# Replace this value with the ARN of the AWS Lambda function you created earlier.
lambda_function_arn = "arn:aws:lambda:REGION-ID:ACCOUNT-ID:function:LogEC2InstanceStateChange"

# Replace this value with the ARN of the AWS IAM service role you created earlier.
cwe_service_role_arn = "arn:aws:iam::ACCOUNT-ID:role/SERVICE-ROLE-NAME"

# Create a rule in Amazon CloudWatch Events.
rule_name = "my-ec2-rule"

# The rule will use this pattern to route the event to the target.
# This pattern is used whenever an Amazon EC2 instance begins running.
event_pattern = {
  "source" => [
    "aws.ec2"
  ],
  "detail-type" => [
    "EC2 Instance State-change Notification"
  ],
  "detail" => {
    "state" => [
      "running"
    ]
  }
}.to_json

cwe.put_rule({
  name: rule_name,
  event_pattern: event_pattern,
  state: "ENABLED",
  role_arn: cwe_service_role_arn
})

# Add a target to the rule.
cwe.put_targets({
  rule: rule_name,
  targets: [
    {
      id: "my-rule-target",
      arn: lambda_function_arn
    }
  ]
})

# To test the rule, stop and then restart an existing Amazon EC2 instance.
# For example:
ec2 = Aws::EC2::Client.new(region: 'us-east-1')

# Replace this with an actual instance ID.
instance_id = "i-INSTANCE-ID"

puts "Attempting to stop the instance. This may take a few minutes..."

ec2.stop_instances({
  instance_ids: [ instance_id ]
})

# Make sure the instance is stopped before attempting to restart it.
ec2.wait_until(:instance_stopped, instance_ids: [ instance_id ])

puts "Attempt to restart the instance. This may take a few minutes..."

ec2.start_instances({
  instance_ids: [ instance_id ]
})

# Make sure the instance is running before continuing on.
ec2.wait_until(:instance_running, instance_ids: [ instance_id ])

# See if and when the rule was triggered.
cw = Aws::CloudWatch::Client.new(region: 'us-east-1')

invocations = cw.get_metric_statistics({
  namespace: "AWS/Events",
  metric_name: "Invocations",
  dimensions: [
    {
      name: "RuleName",
      value: rule_name,
    },
  ],
  start_time: Time.now - 600, # Look back over the past 10 minutes to see if the rule was triggered (10 minutes * 60 seconds = 600 seconds).
  end_time: Time.now,
  period: 60, # Look back every 60 seconds over those past 10 minutes to see how many times the rule may have been triggered.
  statistics: [ "Sum" ],
  unit: "Count"
})

if invocations.datapoints.count > 0
  puts "Rule invocations:"
  invocations.datapoints.each do |datapoint|
    puts "  #{datapoint.sum} invocation(s) at #{datapoint.timestamp}"
  end
else
  puts "No rule invocations."
end

# View the latest related log in Amazon CloudWatch Logs.
cwl = Aws::CloudWatchLogs::Client.new(region: 'us-east-1')

describe_log_streams_response = cwl.describe_log_streams({
  log_group_name: "/aws/lambda/LogEC2InstanceStateChange",
  order_by: "LastEventTime",
  descending: true
})

get_log_events_response = cwl.get_log_events({
  log_group_name: "/aws/lambda/LogEC2InstanceStateChange",
  log_stream_name: describe_log_streams_response.log_streams[0].log_stream_name # Get the latest log stream only.
})

puts "\nLog messages:\n\n"

get_log_events_response.events.each do |event|
  puts event.message
end
