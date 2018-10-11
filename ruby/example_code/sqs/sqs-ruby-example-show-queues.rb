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

require 'aws-sdk-sqs'  # v2: require 'aws-sdk'

sqs = Aws::SQS::Client.new(region: 'us-west-2')

queues = sqs.list_queues

queues.queue_urls.each do |url|
  puts 'URL:                ' + url

  # Get ARN, messages available, and messages in flight for queue
  req = sqs.get_queue_attributes(
    {
      queue_url: url, attribute_names: 
        [
          'QueueArn', 
          'ApproximateNumberOfMessages', 
          'ApproximateNumberOfMessagesNotVisible'
        ]
    }
  )

  arn = req.attributes['QueueArn']
  msgs_available = req.attributes['ApproximateNumberOfMessages']
  msgs_in_flight = req.attributes['ApproximateNumberOfMessagesNotVisible']

  puts 'ARN:                ' + arn
  puts 'Messages available: ' + msgs_available
  puts 'Messages in flight: ' + msgs_in_flight
  puts
end
