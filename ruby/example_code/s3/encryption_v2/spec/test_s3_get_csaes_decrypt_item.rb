# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../s3_get_csaes_decrypt_item"

describe "#get_decrypted_object_content" do
  # Captures the data (metadata and body) put to an Amazon S3 object.
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

  let(:bucket_name) { "doc-example-bucket" }
  let(:object_key) { "my-file.txt" }
  let(:object_content) { "This is the content of my-file.txt." }
  let(:encryption_key_string) { "XSiKrmzhtDKR9tTwJRSLjgwLhiMA82TC2z3GEXAMPLE=" }
  let(:encryption_key) { encryption_key_string.unpack("m")[0] }
  # Note that Aws::S3::EncryptionV2::Client is a wrapper around
  #   Aws::S3::Client. So you must first stub Aws::S3::Client
  #   and then pass it into Aws::S3::EncryptionV2::Client
  #   via the client argument on initialization, as follows.
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_object: { body: object_content }
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

  it "gets the decrypted content of an object in an Amazon S3 bucket" do
    data = stub_put(s3_client)
    s3_encryption_client.put_object(
      bucket: bucket_name,
      key: object_key,
      body: object_content
    )
    stub_get(s3_client, data, true)
    expect(get_decrypted_object_content(
      s3_encryption_client,
      bucket_name,
      object_key
    )).to eq(object_content)
  end
end
