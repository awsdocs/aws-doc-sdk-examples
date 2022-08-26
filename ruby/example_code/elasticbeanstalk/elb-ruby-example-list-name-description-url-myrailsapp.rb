# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# elb-ruby-example-list-name-description-url-myrailsapp.rb demonstrates how to
# get the name and description of an Amazon Elastic Beanstalk application and its
# URL using the AWS SDK for Ruby.

# snippet-start:[eb.Ruby.listNameDescription]

require 'aws-sdk-elasticbeanstalk'  # v2: require 'aws-sdk'
# Overwrite AWS Region with your applicable region.
eb = Aws::ElasticBeanstalk::Client.new(region: 'us-east-1')

apps = eb.describe_applications({application_names: [ARGV[0]]})

if apps.any?
  puts "Name:         #{apps.applications.first.application_name}"
  puts "Description:  #{apps.applications.first.description}"

  envs = eb.describe_environments({application_name: apps.applications.first.application_name})
  puts "URL:          #{envs.environments.first.cname}"
end
# snippet-end:[eb.Ruby.listNameDescription]
