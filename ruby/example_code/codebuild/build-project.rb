# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[codebuild.Ruby.buildProject]
require "aws-sdk-codebuild"  # v2: require 'aws-sdk'

project_name = ""

if ARGV.length != 1
  puts "You must supply the name of the project to build"
  exit 1
else
  project_name = ARGV[0]
end

client = Aws::CodeBuild::Client.new

begin
  client.start_build(project_name: project_name)
  puts "Building project " + project_name
rescue StandardError => ex
  puts "Error building project: " + ex.message
end
# snippet-end:[codebuild.Ruby.buildProject]
