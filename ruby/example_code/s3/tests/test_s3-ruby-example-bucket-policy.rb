# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-bucket-policy'

describe '#bucket_created?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        create_bucket: {
          location: 'http://doc-example-bucket.us-east-1.s3.amazonaws.com/'
        }
      }
    )
  end

  it 'checks whether a bucket was created' do
    expect(bucket_created?(s3_client, bucket_name)).to be(true)
  end
end

describe '#bucket_policy_added?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:aws_principal) { 'arn:aws:iam::111111111111:user/SomeUser' }
  let(:action) { 's3:GetObject' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_policy: {}
      }
    )
  end

  it 'checks whether a bucket policy was added' do
    expect(
      bucket_policy_added?(
        s3_client,
        bucket_name,
        aws_principal,
        action
      )
    ).to be(true)
  end
end

describe '#bucket_policy_aws_principal_updated?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:aws_principal) { 'arn:aws:iam::111111111111:user/SomeUser' }
  let(:new_aws_principal) { 'arn:aws:iam::111111111111:user/SomeOtherUser' }
  let(:action) { 's3:GetObject' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_bucket_policy: {
          policy: "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"#{aws_principal}\"},\"Action\":\"#{action}\",\"Resource\":\"arn:aws:s3:::#{bucket_name}/*\"}]}"
        },
        put_bucket_policy: {}
      }
    )
  end

  it 'checks whether a bucket policy was updated' do
    expect(
      bucket_policy_aws_principal_updated?(
        s3_client,
        bucket_name,
        new_aws_principal
      )
    ).to be(true)
  end
end

describe '#bucket_policy_deleted?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        delete_bucket_policy: {}
      }
    )
  end

  it 'checks whether a bucket policy was deleted' do
    expect(bucket_policy_deleted?(s3_client, bucket_name)).to be(true)
  end
end

describe '#bucket_deleted?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        delete_bucket: {}
      }
    )
  end

  it 'checks whether a bucket was deleted' do
    expect(bucket_deleted?(s3_client, bucket_name)).to be(true)
  end
end
