# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../s3_add_cskms_encrypt_item"

describe "#encrypted_object_uploaded?" do
  let(:bucket_name) { "doc-example-bucket" }
  let(:object_key) { "my-file.txt" }
  let(:object_content) { "This is the content of my-file.txt." }
  let(:kms_key_id) { "9041e78c-7a20-4db3-929e-828abEXAMPLE" }
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
  # Aws::S3::EncryptionV2::Client also requires Aws::KMS::Client for
  #   AWS KMS scenarios. So you must also first stub Aws::KMS::Client
  #   and then pass it into Aws::S3::EncryptionV2::Client
  #   via the kms_client argument on initialization, as follows.
  let(:kms_client) do
    Aws::KMS::Client.new(
      stub_responses: {
        generate_data_key: {
          ciphertext_blob: "AQIDAHh58pZkpaY6Ifx4UPd9kZBp2ssaYk/T1eiY2dnLv9dP1QHFZ71+8g1Yq0ikFTvgjbzgAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMgGqjmfp6p9q3pqgsAgEQgDufNsOEtJJUAsZ6QkJoVUQz9tWgYQ0HMHNtXSObHsRbw6QejNCtGJAxip4/9HJWbmo8V+s+7+NEXAMPLE==",
          key_id: "arn:aws:kms:us-west-2:111111111111:key/9041e78c-7a20-4db3-929e-828abEXAMPLE",
          plaintext: "\xAD\xC8\xF0o\v\x0F\x1E\xD3\xEA\xF8\x05\x03\x9F?\xC2#\xB4_\xE3\xBD\x96\x1A\xC9S\xCA\xE8Ya\xB9{\t\x7F"
        }
      }
    )
  end
  let(:s3_encryption_client) do
    Aws::S3::EncryptionV2::Client.new(
      client: s3_client,
      kms_client: kms_client,
      kms_key_id: kms_key_id,
      key_wrap_schema: :kms_context,
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
