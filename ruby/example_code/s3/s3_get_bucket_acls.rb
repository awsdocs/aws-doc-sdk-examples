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

# Required on Windows
# See: https://github.com/aws/aws-sdk-core-ruby/issues/166
if OS.windows?
  Aws.use_bundled_cert!
end

# We require one arg, the name of the bucket
if ARGV.length != 1
  puts 'You must supply a bucket name'
  exit 1
end

bucket_name = ARGV[0]

client = Aws::S3::Client.new(region: 'us-west-2')

resp = client.get_bucket_acl({bucket: bucket_name})

puts
puts "Owner       #{resp.owner.display_name}"
puts

resp.grants.each do |g|
  if g.grantee.display_name == nil
    puts 'Grantee     EVERYONE'
  else
    puts 'Grantee     ' + g.grantee.display_name
  end

  puts 'ID          ' + g.grantee.id
  puts 'Permission  ' + g.permission
  puts
end
