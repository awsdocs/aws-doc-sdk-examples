# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[codebuild.Ruby.listProjects]
require "aws-sdk-codebuild"  # v2: require 'aws-sdk'

client = Aws::CodeBuild::Client.new

resp = client.list_projects({
  sort_by: "NAME", # accepts NAME, CREATED_TIME, LAST_MODIFIED_TIME
  sort_order: "ASCENDING" # accepts ASCENDING, DESCENDING
})

if resp.projects.empty?
  puts "No projects found!"
else
  resp.projects.each { |p| puts p }
end

puts
# snippet-end:[codebuild.Ruby.listProjects]
