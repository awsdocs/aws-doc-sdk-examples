# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-sns'  # v3: require 'aws-sdk'
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