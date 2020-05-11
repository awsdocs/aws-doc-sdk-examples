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

require 'aws-sdk-sns' # v3: require 'aws-sdk'
require 'rspec'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    class CreateSubscription
      def initialize(*args)
        @client = opts[:createsubscription_client || Aws::CreateSubscription::Client.new]
      end

  def create_subscription()
  resp = @simplenotificationservice.create_subscription
  puts
  puts "Found #{resp.subscription.id} subscription(s)."
  puts


  resp.subscriptions.each do |subscriptions|
    show_subscriptions(subscriptions)
  end
  end
end

    private

    def show_subscription(subscription)
      puts "Protocol: #{subscription.protocol}"
      puts "Endpoint: #{subscription.endpoint}"
      puts 'Email:'

      if !subscription.email.nil?
        subscription.email.each do |e|
          puts "  ARN:  #{e.email_arn}"
        end
      end
      puts
      end
      end

