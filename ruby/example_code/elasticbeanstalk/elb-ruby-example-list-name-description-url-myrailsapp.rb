# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# elb-ruby-example-list-name-description-url-myrailsapp.rb demonstrates how to
# get the name and description of an Elastic Beanstalk application and its
# URL using the AWS SKD for Ruby.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[eb.Ruby.listNameDescription]

require 'aws-sdk-elasticbeanstalk'  # v2: require 'aws-sdk'

eb = Aws::ElasticBeanstalk::Client.new(region: 'REGION')

app = eb.describe_applications({application_names: [args[0]]})

if app.exists?
  puts "Name:         #{app.application_name}"
  puts "Description:  #{app.description}"

  envs = eb.describe_environments({application_name: app.application_name})
  puts "URL:          #{envs.environments[0].cname}"
end
# snippet-end:[eb.Ruby.listNameDescription]
