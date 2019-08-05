# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Gives read access, by email address, to an S3 bucket.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[get_bucket_acl method]
# snippet-keyword:[put_bucket_acl method]
# snippet-keyword:[Ruby]
# snippet-service:[s3]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

permission = 'READ'

if ARGV.length < 2
  puts 'You must supply a bucket name and email address'
  exit 1
end

bucket_name = ARGV[0]
address = ARGV[1]

if ARGV.length > 2
  permission = ARGV[2]
end

client = Aws::S3::Client.new(region: 'us-west-2')

resp = client.get_bucket_acl({bucket: bucket_name})

owner = resp.owner.display_name
owner_id = resp.owner.id

grants = resp.grants

# Create new grantee
new_grant = {
    grantee: {
        email_address: address,
        type: 'AmazonCustomerByEmail',
    },
    permission: permission,
}

# Add them to the existing grants
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
}

client.put_bucket_acl(params)
