# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# elb-ruby-example-list-all-apps.rb demonstrates how to list your Amazon
# Elastic Beanstalk applications and environments using the AWS SDK for Ruby.

# snippet-start:[eb.Ruby.listApps]

require "aws-sdk-elasticbeanstalk"  # v2: require 'aws-sdk'

# Replace us-west-2 with the AWS Region you're using for Elastic Beanstalk.
eb = Aws::ElasticBeanstalk::Client.new(region: "us-west-2")

eb.describe_applications.applications.each do |a|
  puts "Name:         #{a.application_name}"
  puts "Description:  #{a.description}"

  eb.describe_environments({application_name: a.application_name}).environments.each do |env|
    puts "  Environment:  #{env.environment_name}"
    puts "    URL:        #{env.cname}"
    puts "    Health:     #{env.health}"
  end
end
# snippet-end:[eb.Ruby.listApps]
