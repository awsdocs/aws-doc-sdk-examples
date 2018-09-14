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

# Demonstrates how to:
# 1. Create an Amazon EC2 key pair. 
# 2. Get information about available key pairs.
# 3. Delete the key pair.

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

key_pair_name = "my-key-pair"

# Create a key pair.
begin
  key_pair = ec2.create_key_pair({
    key_name: key_pair_name
  })
  puts "Created key pair '#{key_pair.key_name}'."
  puts "\nSHA-1 digest of the DER encoded private key:"
  puts "#{key_pair.key_fingerprint}"
  puts "\nUnencrypted PEM encoded RSA private key:"
  puts "#{key_pair.key_material}"
rescue Aws::EC2::Errors::InvalidKeyPairDuplicate
  puts "A key pair named '#{key_pair_name}' already exists."
end

# Get information about Amazon EC2 key pairs.
key_pairs_result = ec2.describe_key_pairs()

if key_pairs_result.key_pairs.count > 0
  puts "\nKey pair names:"
  key_pairs_result.key_pairs.each do |key_pair|
    puts key_pair.key_name
  end
end

# Delete the key pair.
ec2.delete_key_pair({
  key_name: key_pair_name 
})


