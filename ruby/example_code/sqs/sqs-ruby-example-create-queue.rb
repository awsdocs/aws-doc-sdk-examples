/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates an SQS queue.]
# snippet-keyword:[Amazon Simple Queue Service]
# snippet-keyword:[create_queue method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sqs]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]


require 'aws-sdk-sqs'  # v2: require 'aws-sdk'
# Create a new SES resource in the us-west-2 region.
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
sqs = Aws::SQS::Client.new(region: 'us-west-2')
module Aws
  module SimpleQueueService
    class CreateQueue
      def initialize(options = {})
        @name = options[:name]
        @url = options[:url]
        @arn = options[:arn]
        @attributes = options[:attributes]
      end
      end
    end
  end
end


queue = sqs.create_queue(queue_name: 'MyGroovyQueue')

if queue.exists?
puts queue.queue_url
end