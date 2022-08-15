# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# decrypt_data.rb demonstrates how to decrypt a string
# using Amazon Key Management Services (AWS KMS) using the AWS SDK for Ruby.

# snippet-start:[kms.ruby.decryptBlob]

require "aws-sdk-kms" # v2: require 'aws-sdk'

# Decrypted blob

blob = "01020200785d68faeec386af1057904926253051eb2919d3c16078badf65b808b26dd057c101747cadf3593596e093d4ffbf22434a6d00000068306606092a864886f70d010706a0593057020100305206092a864886f70d010701301e060960864801650304012e3011040c9d629e573683972cdb7d94b30201108025b20b060591b02ca0deb0fbdfc2f86c8bfcb265947739851ad56f3adce91eba87c59691a9a1"
blob_packed = [blob].pack("H*")

client = Aws::KMS::Client.new(region: "us-west-2")

resp = client.decrypt({
                        ciphertext_blob: blob_packed
                      })

puts "Raw text: "
puts resp.plaintext
# snippet-end:[kms.ruby.decryptBlob]
