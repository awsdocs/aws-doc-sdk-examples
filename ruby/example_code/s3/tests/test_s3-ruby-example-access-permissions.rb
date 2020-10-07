# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-access-permissions'

describe '#bucket_acl_set?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:access_level) { 'private' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_acl: {}
      }
    )
  end

  it 'sets the ACL of a bucket for everyone' do
    expect(
      bucket_acl_set?(
        s3_client,
        bucket_name,
        access_level
      )
    ).to be(true)
  end
end

describe '#object_uploaded?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:object_content) { 'This is the content of my-file.txt.' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {}
      }
    )
  end

  it 'uploads an object to a bucket' do
    expect(
      object_uploaded?(
        s3_client,
        bucket_name,
        object_key,
        object_content
      )
    ).to be(true)
  end
end

describe '#object_acl_set?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:access_level) { 'private' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object_acl: {}
      }
    )
  end

  it 'sets the ACL of an object for everyone' do
    expect(
      object_acl_set?(
        s3_client,
        bucket_name,
        object_key,
        access_level
      )
    ).to be(true)
  end
end
