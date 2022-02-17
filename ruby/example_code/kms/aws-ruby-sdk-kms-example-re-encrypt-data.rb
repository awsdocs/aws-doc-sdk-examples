# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-kms-example-re-encrypt-data.rb demonstrates how to
# re-encrypt data under a new customer master key (CMK)
# using Amazon Key Management Services (AWS KMS) using the AWS SDK for Ruby.

# snippet-start:[kms.ruby.reEncryptData]

require 'aws-sdk-kms'  # v2: require 'aws-sdk'

# Human-readable version of the ciphertext of the data to reencrypt.

blob = '01020200785d68faeec386af1057904926253051eb2919d3c16078badf65b808b26dd057c101747cadf3593596e093d4ffbf22434a6d00000068306606092a864886f70d010706a0593057020100305206092a864886f70d010701301e060960864801650304012e3011040c9d629e573683972cdb7d94b30201108025b20b060591b02ca0deb0fbdfc2f86c8bfcb265947739851ad56f3adce91eba87c59691a9a1'
sourceCiphertextBlob = [blob].pack("H*")

# Replace the fictitious key ARN with a valid key ID

destinationKeyId = 'arn:aws:kms:us-west-2:111122223333:key/0987dcba-09fe-87dc-65ba-ab0987654321'

client = Aws::KMS::Client.new(region: 'us-west-2')

resp = client.re_encrypt({
  ciphertext_blob: sourceCiphertextBlob,
  destination_key_id: destinationKeyId
})

puts 'Blob:'
puts resp.ciphertext_blob.unpack('H*')
# snippet-end:[kms.ruby.reEncryptData]
