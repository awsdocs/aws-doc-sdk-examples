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

require 'aws-sdk-s3'  # v2: require 'aws-sdk'
require 'os'

# Required on Windows
# See: https://github.com/aws/aws-sdk-core-ruby/issues/166
if OS.windows?
  Aws.use_bundled_cert!
end

# main
permission = 'READ'

if ARGV.length < 3
  puts 'You must supply a bucket name, object name, and email address'
  exit 1
end

client = Aws::S3::Client.new(region: 'us-west-2')

bucket_name = ARGV[0]
object_name = ARGV[1]
address = ARGV[2]

if ARGV.length > 3
  permission = ARGV[3]
end

resp = client.get_object_acl({bucket: bucket_name, key: object_name})

owner = resp.owner.display_name
owner_id = resp.owner.id

# Keep existing grants
grants = resp.grants

# Create new grantee to add to grants
new_grant = {
    grantee: {
        email_address: address,
        type: 'AmazonCustomerByEmail',
    },
    permission: permission,
}

# Add new grantee to grants
grants << new_grant

params = {
    access_control_policy: {
        grants: grants,
        owner: {
            display_name: owner,
            id: owner_id,
        },
    },
    bucket: bucket_name,
    key: object_name,
}

client.put_object_acl(params)
