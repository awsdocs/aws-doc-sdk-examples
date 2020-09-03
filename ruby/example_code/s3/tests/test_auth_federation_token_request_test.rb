# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_federation_token_request_test.rb'

describe '#get_user' do
  let(:iam) { Aws::IAM::Client.new(stub_responses: true) }
  let(:user_name) { 'my-user' }

  it "confirms the specified user's name" do
    user_data = iam.stub_data(:get_user, user: { user_name: user_name })
    iam.stub_responses(:get_user, user_data)
    user = get_user(iam, user_name)
    expect(user.user_name).to eq(user_name)
  end
end

describe '#get_temporary_credentials' do
  let(:sts) { Aws::STS::Client.new(stub_responses: true) }
  let(:access_key_id) { 'AKIAIOSFODNN7EXAMPLE' }
  let(:duration_seconds) { 3600 }
  let(:user_name) { 'my-user' }
  let(:policy) {
    {
      'Version' => '2012-10-17',
      'Statement' => [
        'Sid' => 'Stmt1',
        'Effect' => 'Allow',
        'Action' => 's3:ListBucket',
        'Resource' => 'arn:aws:s3:::my-bucket'
      ]
    }
  }

  it 'gets temporary credentials for the specified user and policy' do
    credentials_data = sts.stub_data(:get_federation_token,
      credentials: { access_key_id: access_key_id})
    sts.stub_responses(:get_federation_token, credentials_data)
    credentials = get_temporary_credentials(sts, duration_seconds, user_name, policy)
    expect(credentials.access_key_id).to eq(access_key_id)
  end
end

describe '#list_objects_in_bucket?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'my-bucket' }

  it "lists the objects' keys and ETags in the specified bucket" do
    objects_data = s3_client.stub_data(:list_objects_v2,
      contents: [
        { key: 'my-file-1.txt',
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
