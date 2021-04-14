# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Adds an event notification to an Amazon Simple Storage Service
#   (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket.
# - For an event notification to AWS Lambda, a Lambda function.
# - For an event notification to Amazon Simple Notification Service
#   (Amazon SNS), an SNS topic.
# - For an event notification to Amazon Simple Queue Service
#   (Amazon SQS), an SQS queue.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The name of the bucket.
# @param events [Array] The S3 events to notify on.
# @param send_to_type [String] The type of AWS resource to notify. Allowed
#   values include 'lambda' for Lambda, 'sns' for SNS, and 'sqs' for SQS.
# @param resource_arn [String] The Amazon Resource Name (ARN) of the
#   AWS resource.
# @return [Boolean] true if the bucket notification configuration was set;
#   otherwise, false.
# @example
#   exit 1 unless bucket_notification_configuration_set?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     ['s3:ObjectCreated:*'],
#     'sns',
#     'arn:aws:sns:us-east-1:111111111111:my-topic'
#   )
def bucket_notification_configuration_set?(
  s3_client,
  bucket_name,
  events,
  send_to_type,
  resource_arn
)
  case send_to_type
  when 'lambda'
    s3_client.put_bucket_notification_configuration(
      bucket: bucket_name,
      notification_configuration: {
        lambda_function_configurations: [
          {
            lambda_function_arn: resource_arn,
            events: events
          }
        ]
      }
    )
  when 'sns'
    s3_client.put_bucket_notification_configuration(
      bucket: bucket_name,
      notification_configuration: {
        topic_configurations: [
          {
            topic_arn: resource_arn,
            events: events
          }
        ]
      }
    )
  when 'sqs'
    s3_client.put_bucket_notification_configuration(
      bucket: bucket_name,
      notification_configuration: {
        queue_configurations: [
          {
            queue_arn: resource_arn,
            events: events
          }
        ]
      }
    )
  else
    puts 'Error setting bucket notification configuration: ' \
      "Cannot determine send-to type. Must be 'lambda', 'sns', or 'sqs'."
    return false
  end
  return true
rescue StandardError => e
  puts "Error setting bucket notification configuration: #{e.message}"
  return false
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  events = ['s3:ObjectCreated:*']

  # For an SNS topic:
  send_to_type = 'sns'
  resource_arn = 'arn:aws:sns:us-east-1:111111111111:my-topic'

  # For an SQS queue:
  # send_to_type = 'sqs'
  # resource_arn = 'arn:aws:sqs:us-east-1:111111111111:my-queue'

  # For a Lambda function:
  # send_to_type = 'lambda'
  # resource_arn = 'arn:aws:lambda:us-east-1:111111111111:function:myFunction'

  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_notification_configuration_set?(
    s3_client,
    bucket_name,
    events,
    send_to_type,
    resource_arn
  )
    puts 'Bucket notification configuration set.'
  else
    puts 'Bucket notification configuration not set.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
