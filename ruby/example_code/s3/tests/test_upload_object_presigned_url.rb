# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../upload_object_presigned_url'

describe '#object_uploaded_to_presigned_url?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:object_content) { StringIO.new('This is the content of my-file.txt.') }
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:s3_resource) do
    Aws::S3::Resource.new(
      client: s3_client,
      stub_responses: {
        bucket: {
          name: bucket_name
        },
        object: {
          key: object_key
        },
        presigned_url: "https://#{bucket_name}.s3.amazonaws.com/#{object_key}?AWSAccessKeyId=AKIAEXAMPLEACCESSKEY&Signature=EXHCcBe%EXAMPLEKnz3r8O0AgEXAMPLE&Expires=1555531131",
        get: {
          body: object_content
        }
      }
    )
  end

  it 'checks whether the object was uploaded to the presigned URL' do
    http_client = double('Net::HTTP')
    allow(http_client).to receive(:start).with(any_args)
    expect(
      object_uploaded_to_presigned_url?(
        s3_resource,
        bucket_name,
        object_key,
        object_content,
        http_client
      )
    ).to be(true)
  end
end
