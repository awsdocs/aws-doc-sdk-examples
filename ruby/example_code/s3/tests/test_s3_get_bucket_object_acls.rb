# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_get_bucket_object_acls'

describe '#list_object_acls' do
  let(:display_name) { 'Mary Doe' }
  let(:id) { 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_object_acl: {
          owner: {
            display_name: display_name,
            id: id
          },
          grants: [
            {
              grantee: {
                display_name: display_name,
                id: id,
                type: 'CanonicalUser'
              },
              permission: 'FULL_CONTROL'
            }
          ]
        }
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }

  it 'lists the access control lists for an object in a bucket' do
    expect(list_object_acls(s3_client, bucket_name, object_key)).to be
  end
end
