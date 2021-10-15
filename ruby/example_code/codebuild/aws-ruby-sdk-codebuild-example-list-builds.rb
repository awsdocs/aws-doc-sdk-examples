# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-codebuild-example-list-builds.rb demonstrates how to list your
# AWS CodeBuild builds using the AWS SDK for Ruby.

# snippet-start:[codebuild.Ruby.listBuilds]

require 'aws-sdk-codebuild'  # v2: require 'aws-sdk'

# Replace us-west-2 with the AWS Region you're using for Amazon CodeBuild.
client = Aws::CodeBuild::Client.new(region: 'us-west-2')

build_list = client.list_builds({sort_order: 'ASCENDING', })

builds = client.batch_get_builds({ids: build_list.ids})

builds.builds.each do |build|
  puts 'Project:    ' + build.project_name
  puts 'Phase:      ' + build.current_phase
  puts 'Status:     ' + build.build_status
end
# snippet-end:[codebuild.Ruby.listBuilds]
