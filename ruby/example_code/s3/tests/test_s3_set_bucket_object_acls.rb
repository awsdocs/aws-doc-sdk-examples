# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_set_bucket_object_acls'

describe '#object_acl_set_for_owner_id?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:permission) { 'READ' }
  let(:owner_id) { 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object_acl: {}
      }
    )
  end

  it 'sets the ACL of an object based on an owner ID' do
    expect(
      object_acl_set_for_owner_id?(
        s3_client,
        bucket_name,
        object_key,
        permission,
        owner_id
      )
    ).to be(true)
  end
end
