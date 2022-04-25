# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../s3_get_cskms_decrypt_item"

describe "#get_decrypted_object_content" do
  # Captures the data (metadata and body) put to an Amason S3 object.
  def stub_put(s3_client)
    data = {}
    s3_client.stub_responses(:put_object, lambda { |context|
      data[:metadata] = context.params[:metadata]
      data[:enc_body] = context.params[:body].read
      {}
    })
    data
  end

  # Given data from stub_put, stub a getter for the same object.
  # During the get operation, get_object is called twice: once to
  #   get the full body, and once again with a range to get just the
  #   auth_tag.
  def stub_get(s3_client, data, stub_auth_tag)
    resp_headers = Hash[*data[:metadata].flat_map { |k, v| ["x-amz-meta-#{k}", v] }]
    resp_headers["content-length"] = data[:enc_body].length
    if stub_auth_tag
      auth_tag = data[:enc_body].unpack("C*")[-16, 16].pack("C*")
    else
      auth_tag = nil
    end
    s3_client.stub_responses(
      :get_object,
      {
        status_code: 200,
        body: data[:enc_body],
        headers: resp_headers
      },
      {
        body: auth_tag
      }
    )
  end

  def stub_decrypt(kms_client, opts)
    kms_client.stub_responses(
      :decrypt, lambda do |context|
      if opts[:any_kms_key]
        expect(context.params["key_id"]).to be_nil
      else
        if opts[:raise] && context.params["key_id"] != opts[:response][:key_id]
          raise Aws::KMS::Errors::IncorrectKeyException.new(context, "")
        else
          expect(context.params[:key_id]).to eq(opts[:response][:key_id])
        end
      end
      opts[:response]
      end
    )
  end

  let(:bucket_name) { "doc-example-bucket" }
  let(:object_key) { "my-file.txt" }
  let(:object_content) { "This is the content of my-file.txt." }
  let(:kms_key_id) { "9041e78c-7a20-4db3-929e-828abEXAMPLE" }
  let(:kms_ciphertext_blob) { Base64.decode64("AQIDAHiWj6qDEnwihp7W7g6VZb1xqsat5jdSUdEaGhgZepHdLAGASCQI7LZz\nz7GzCpm6y4sHAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEH\nATAeBglghkgBZQMEAS4wEQQMJMJe6d8DkRTWwlvtAgEQgDtBCwiibCTS8pb7\n6BYKklVjy+CmO9q3r6y4u/9jJ8lk9eg5GwiskmcBtPMcWogMzx/vh+/65Cjb\nsQBpLQ==\n") }
  let(:kms_plaintext) { Base64.decode64("5V7JWe+UDRhv66TaDg+tP6JONf/GkTdXk6Jq61weM+w=\n") }
  # Note that Aws::S3::EncryptionV2::Client is a wrapper around
  #   Aws::S3::Client. So you must first stub Aws::S3::Client
  #   and then pass it into Aws::S3::EncryptionV2::Client
  #   via the client argument on initialization, as follows.
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_object: {
          body: object_content
        }
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
          ciphertext_blob: kms_ciphertext_blob,
          key_id: "arn:aws:kms:us-west-2:111111111111:key/9041e78c-7a20-4db3-929e-828abEXAMPLE",
          plaintext: kms_plaintext
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

  it "gets the decrypted content of an object in an Amazon S3 bucket" do
    data = stub_put(s3_client)
    s3_encryption_client.put_object(
      bucket: bucket_name,
      key: object_key,
      body: object_content
    )
    stub_get(s3_client, data, true)
    stub_decrypt(kms_client, any_kms_key: false, response:
      {
        key_id: kms_key_id,
        plaintext: kms_plaintext,
        encryption_algorithm: "SYMMETRIC_DEFAULT"
      }
    )
    expect(get_decrypted_object_content(
      s3_encryption_client,
      bucket_name,
      object_key
    )).to eq(object_content)
  end
end
