# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_set_bucket_acls'

describe '#bucket_acl_set_for_owner_id?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:permission) { 'READ' }
  let(:owner_id) { 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_acl: {}
      }
    )
  end

  it 'sets the ACL of a bucket based on an owner ID' do
    expect(
      bucket_acl_set_for_owner_id?(
        s3_client,
        bucket_name,
        permission,
        owner_id
      )
    ).to be(true)
  end
end
