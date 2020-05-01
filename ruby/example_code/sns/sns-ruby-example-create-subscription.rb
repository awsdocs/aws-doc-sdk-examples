/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    specific language governing permissions and limitations under the License.
*/

# frozen_string_literal: true

# snippet-comment:
# [These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Subscribes a user to an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[topic method]
# snippet-keyword:[Topic.subscribe method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

require 'aws-sdk-sns' # v2: require 'aws-sdk'
require 'rspec'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    class CreateSubscription
      # @option options [required, String] :arn
      # @option options [required, String] :protocol
      # @option options [required, String] :endpoint
      # @api private
      def initialize(options = {})
        @arn = options[:arn]
        @protocol = options[:protocol]
        @endpoint = options[:endpoint]
      end

      # @return [String] The Topic ARN of the topic you created earlier
      # e.g. "arn:aws:sns:us-west-2:123456789012:MyTopic".
      attr_reader :arn

      # @return [String] Chosen endpoint type, e.g. "email".
      attr_reader :protocol

      # @return [String] Email address that can receive notifications
      # e.g. "peccy@amazon.com",
      attr_reader :endpoint
    end
  end
end

# If the subscription exists,
# a subscription confirmation with your subscription ID is displayed
subscription = sns.describe_subscription({ subscriptionid: [args[0]] })

if subscription.exists?
  puts "Subscription ID:     #{subscription.subscriptionid}"
end
