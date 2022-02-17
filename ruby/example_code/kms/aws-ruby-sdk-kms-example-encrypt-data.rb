# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-kms-example-encrypt-data.rb demonstrates how to encrypt a string
# using Amazon Key Management Services (AWS KMS) using the AWS SDK for Ruby.

# snippet-start:[kms.ruby.encryptBlob]
require 'aws-sdk-kms'  # v2: require 'aws-sdk'

# ARN of the customer master key (CMK).
#
# Replace the fictitious key ARN with a valid key ID

keyId = 'arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab'

text = '1234567890'

client = Aws::KMS::Client.new(region: 'us-west-2')

resp = client.encrypt({
  key_id: keyId,
  plaintext: text,
})

puts 'Blob:'
puts resp.ciphertext_blob.unpack('H*')
# snippet-end:[kms.ruby.encryptBlob]
