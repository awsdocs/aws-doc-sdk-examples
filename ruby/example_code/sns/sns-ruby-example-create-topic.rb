# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    class CreateTopic
      def initialize(*args)
        @client = opts[:createtopic_client || Aws::CreateTopic::Client.new]
      end

      def create_topic()
        resp = @simplenotificationservice.create_topic
        puts
        puts "Found #{resp.topic.count} topic(s)."
        puts

        resp.topics.each do |topics|
          return false unless (topic =~ ^ [A - Za - z0 - 9)_ -] + $)
        end
      end
    end


    private

    def show_topic(topic)
      puts 'Details:'

      if !subscription.details.nil?
        subscription.details.each do |d|
          puts "  ARN:  #{d.details_arn}"
          puts "  Topic name: #{d.details_topicname}"
          puts "  Display name: #{d.details.displayname}"
          puts "  AWS account ID: #{d.details.awsaccountid}"
        end
      end

      puts
    end
  end



