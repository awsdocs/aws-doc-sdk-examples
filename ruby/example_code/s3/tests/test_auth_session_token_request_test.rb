# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_session_token_request_test'

describe '#user_exists?' do
  let(:iam_client) { Aws::IAM::Client.new(stub_responses: true) }
  let(:user_name) { 'my-user' }

  it "confirms the specified user's name" do
    user_data = iam_client.stub_data(:get_user, user: { user_name: user_name })
    iam_client.stub_responses(:get_user, user_data)
    expect(user_exists?(iam_client, user_name)).to be(true)
  end
end

describe '#create_user' do
  let(:iam_client) { Aws::IAM::Client.new(stub_responses: true) }
  let(:user_name) { 'my-user' }

  it "confirms the new user's name" do
    user_data = iam_client.stub_data(:create_user, user: { user_name: user_name })
    iam_client.stub_responses(:create_user, user_data)
    user = create_user(iam_client, user_name)
    expect(user.user_name).to eq(user_name)
  end
end

describe '#get_user' do
  let(:iam_client) { Aws::IAM::Client.new(stub_responses: true) }
  let(:user_name) { 'my-user' }

  it "confirms the existing user's name" do
    user_data = iam_client.stub_data(:get_user, user: { user_name: user_name })
    iam_client.stub_responses(:get_user, user_data)
    user = get_user(iam_client, user_name)
    expect(user.user_name).to eq(user_name)
  end
end

describe '#role_exists?' do
  let(:iam_client) { Aws::IAM::Client.new(stub_responses: true) }
  let(:role_name) { 'my-role' }

  it "confirms the role's name" do
    role_data = iam_client.stub_data(:get_role, role: { role_name: role_name })
    iam_client.stub_responses(:get_role, role_data)
    expect(role_exists?(iam_client, role_name)).to be(true)
  end
end

describe '#get_credentials' do
  let(:sts_client) { Aws::STS::Client.new(stub_responses: true) }
  let(:role_arn) { 'arn:aws:iam::123456789012:role/AmazonS3ReadOnly' }
  let(:role_session_name) { 'ReadAmazonS3Bucket' }
  let(:duration_seconds) { 3600 }

  it "confirms the role's ARN" do
    sts_client.stub_responses(
      :assume_role,
      credentials: {
        access_key_id: 'AKIAIOSFODNN7EXAMPLE',
        secret_access_key: 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY',
        session_token: 'IQoJb3Jp...555xtw==',
        expiration: Time.now + 3600
      }
    )
    credentials = get_credentials(sts_client, role_arn, role_session_name, duration_seconds)
    expect(credentials.credentials.access_key_id).to eq('AKIAIOSFODNN7EXAMPLE')
  end
end

describe '#bucket_exists?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }

  it 'checks whether the bucket exists' do
    buckets_data = s3_client.stub_data(
      :list_buckets,
      buckets: [{ name: bucket_name }]
    )
    s3_client.stub_responses(:list_buckets, buckets_data)
    expect(bucket_exists?(s3_client, bucket_name)).to be(true)
  end
end

describe '#list_objects_in_bucket?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }

  it "lists the objects' keys and ETags in the specified bucket" do
    objects_data = s3_client.stub_data(
      :list_objects_v2,
      contents: [
        {
          key: 'my-file-1.txt',
          etag: '265bada5d91e2b3f19bbad42c4f0dd99'
        },
        {
          key: 'my-file-2.txt',
          etag: '265bada5d91e2b3f19bbad42c4f0dd00'
        }
      ]
    )
    s3_client.stub_responses(:list_objects_v2, objects_data)
    expect(list_objects_in_bucket?(s3_client, bucket_name)).to be(true)
  end
end
