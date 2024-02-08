# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[codebuild.Ruby.listBuilds]
require "aws-sdk-codebuild"  # v2: require 'aws-sdk'

client = Aws::CodeBuild::Client.new

build_list = client.list_builds({sort_order: "ASCENDING", })

if build_list.ids.empty?
  puts "No builds found!"
else
  builds = client.batch_get_builds({ids: build_list.ids})

  builds.builds.each do |build|
    puts "Project:    " + build.project_name
    puts "Phase:      " + build.current_phase
    puts "Status:     " + build.build_status
  end
end
# snippet-end:[codebuild.Ruby.listBuilds]
