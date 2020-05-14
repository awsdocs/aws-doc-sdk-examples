# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    class EnableResource
      def initialize(*args)
      @client = opts[:enableresource_client || Aws::EnableResource::Client.new]
    end

    def enable_resource()
      resp = @simplenotificationservice.enable_resource

      puts
      puts "Found #{resp.resource.count} resource(s)."
      puts

    resp.resources.each do |resource|
      show_resource(resource)
    end
  end

    private

    def show_resources(resource)
    puts "Attribute Name: #{resource.attributename}"
    puts "Attribute Value: #{resource.attributevalue}"
    puts 'ARN:'

    if !resource.topic.nil?
      resource.each do |r|
        puts "  ARN:  #{r.topic_arn}"
      end
    end

    puts
  end
end




