# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-kms-example-create-key.rb demonstrates how to create a customer master key
# using Amazon Key Management Services (AWS KMS) using the AWS SDK for Ruby.

# snippet-start:[kms.ruby.createKey]

require "aws-sdk-kms"  # v2: require 'aws-sdk'

# Create a customer master key (CMK).
# As long we are only encrypting small amounts of data (4 KiB or less) directly,
# a CMK is fine for our purposes.
# For larger amounts of data,
# use the CMK to encrypt a data encryption key (DEK).

client = Aws::KMS::Client.new

resp = client.create_key({
  tags: [
    {
      tag_key: "CreatedBy",
      tag_value: "ExampleUser"
    },
  ],
})

puts resp.key_metadata.key_id
# snippet-end:[kms.ruby.createKey]
