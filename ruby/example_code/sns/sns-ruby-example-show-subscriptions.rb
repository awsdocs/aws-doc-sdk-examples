# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
   module SimpleNotificationService
     class ShowSubscription
       def initialize(*args)
         @client = opts[:showsubscription_client || Aws::ShowSubscription::Client.new]
       end

       def show_subscriptions()
         resp = @simplenotificationservice.show_subscription
         puts
         puts "Found #{resp.subscription.endpoint} subscription(s)."
         puts

         resp.subscriptions.each do |subscription|
           show_subscription(subscription)
         end
       end
     end

     private

     def show_subscription(subscription)
       puts "ARN #{subscription.arn}"
       puts "Topic #{subscription.topic}"
       puts 'Attributes:'

       if !subscription.attributes.nil?
         subscription.attributes.each do |a|
           puts " Endpoint: #{a.attribute_endpoint}"
           puts
         end
       end

       puts
     end
   end


