# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_get_bucket_acls'

describe '#list_bucket_acls' do
  let(:display_name) { 'Mary Doe' }
  let(:id) { 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_bucket_acl: {
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

  it 'lists the access control lists for a bucket' do
    expect(list_bucket_acls(s3_client, bucket_name)).to be
  end
end
