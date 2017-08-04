# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk'
require 'os'

if OS.windows?
  Aws.use_bundled_cert!
end

# main
project_name = ''

if ARGV.length != 1
  puts 'You must supply the name of the project to build'
  exit 1
else
      project_name = ARGV[0]
end

client = Aws::CodeBuild::Client.new(region: 'us-west-2')

begin
  resp = client.start_build({project_name: project_name, })

  puts 'Building project'
rescue Exception => ex
  puts 'Error building project: ' + ex.message
end
