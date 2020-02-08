# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Gets an S3 bucket item using an RSA private key.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[Encryption.get_object method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
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

require 'aws-sdk-s3' # v2: require 'aws-sdk'
require 'openssl'

if ARGV.empty?()
  puts 'You must supply a pass phrase'
  exit 1
end

pass_phrase = ARGV[0]

bucket = 'my_bucket'
item = 'my_item'
key_file = 'private_key.pem'

begin
  private_key = File.binread(key_file)
  key = OpenSSL::PKey::RSA.new(private_key, pass_phrase)

  # encryption client
  enc_client = Aws::S3::Encryption::Client.new(encryption_key: key)

  resp = enc_client.get_object(bucket: bucket, key: item)

  puts resp.body.read
rescue StandardError => ex
  puts 'Could not get item'
  puts 'Error message:'
  puts ex.message
end
