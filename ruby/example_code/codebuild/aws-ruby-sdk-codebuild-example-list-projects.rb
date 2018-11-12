#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Describes your CodeBuild projects.]
#snippet-keyword:[AWS CodeBuild]
#snippet-keyword:[list_projects method]
#snippet-keyword:[Ruby]
#snippet-service:[codebuild]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-codebuild'  # v2: require 'aws-sdk'

client = Aws::CodeBuild::Client.new(region: 'us-west-2')

resp = client.list_projects({
  sort_by: 'NAME', # accepts NAME, CREATED_TIME, LAST_MODIFIED_TIME
  sort_order: 'ASCENDING' # accepts ASCENDING, DESCENDING
})

resp.projects.each { |p| puts p }

puts
