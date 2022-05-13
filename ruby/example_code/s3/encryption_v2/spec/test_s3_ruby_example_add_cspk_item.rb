# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../s3-ruby-example-add-cspk-item"

describe "#object_uploaded_with_public_key_encryption?" do
  let(:bucket_name) { "doc-example-bucket" }
  let(:object_key) { "my-file.txt" }
  let(:object_content) { "This is the content of my-file.txt." }
  # Note that Aws::S3::EncryptionV2::Client is a wrapper around
  #   Aws::S3::Client. So you must first stub Aws::S3::Client
  #   and then pass it into Aws::S3::EncryptionV2::Client
  #   via the client argument on initialization, as follows.
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {}
      }
    )
  end
  let(:public_key) { OpenSSL::PKey::RSA.new(2048) }
  let(:s3_encryption_client) do
    Aws::S3::EncryptionV2::Client.new(
      client: s3_client,
      encryption_key: public_key,
      key_wrap_schema: :rsa_oaep_sha1,
      content_encryption_schema: :aes_gcm_no_padding,
      security_profile: :v2
    )
  end

  it "uploads an object to a bucket with public key encryption" do
    expect(
      object_uploaded_with_public_key_encryption?(
        s3_encryption_client,
        bucket_name,
        object_key,
        object_content
      )
    ).to be(true)
  end
end
