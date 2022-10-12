# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-codebuild-example-list-projects.rb demonstrates how to list your
# AWS CodeBuild projects using the AWS SDK for Ruby.

# snippet-start:[codebuild.Ruby.listProjects]

require "aws-sdk-codebuild"  # v2: require 'aws-sdk'

# Replace us-west-2 with the AWS Region you're using for Amazon CodeBuild.
client = Aws::CodeBuild::Client.new(region: "REGION")

resp = client.list_projects({
  sort_by: "NAME", # accepts NAME, CREATED_TIME, LAST_MODIFIED_TIME
  sort_order: "ASCENDING" # accepts ASCENDING, DESCENDING
})

resp.projects.each { |p| puts p }

puts
# snippet-end:[codebuild.Ruby.listProjects]
