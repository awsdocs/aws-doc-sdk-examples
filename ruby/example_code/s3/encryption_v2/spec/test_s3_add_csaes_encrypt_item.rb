# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../s3_add_csaes_encrypt_item"

describe "#encrypted_object_uploaded?" do
  let(:bucket_name) { "doc-example-bucket" }
  let(:object_key) { "my-file.txt" }
  let(:object_content) { "This is the content of my-file.txt." }
  let(:encryption_key) { get_random_aes_256_gcm_key }
  # Note that Aws::S3::EncryptionV2::Client is a wrapper around
  #   Aws::S3::Client. So you must first stub Aws::S3::Client
  #   and then pass it into Aws::S3::EncryptionV2::Client
  #   via the client argument on initialization, as follows.
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: { etag: "8c009a8b36167046a47caee5b3639de4" }
      }
    )
  end
  let(:s3_encryption_client) do
    Aws::S3::EncryptionV2::Client.new(
      client: s3_client,
      encryption_key: encryption_key,
      key_wrap_schema: :aes_gcm,
      content_encryption_schema: :aes_gcm_no_padding,
      security_profile: :v2
    )
  end

  it "uploads an encrypted object to an Amazon S3 bucket" do
    expect(encrypted_object_uploaded?(
      s3_encryption_client,
      bucket_name,
      object_key,
      object_content
    )).to be(true)
  end
end
