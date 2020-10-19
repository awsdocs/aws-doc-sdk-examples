# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-find-open-buckets'

describe '#get_open_buckets' do
  let(:region) { 'us-east-1' }
  let(:grantee_display_name) { 'Mary Doe' }
  let(:grantee_id) { 'examplee7a2f25102679df27bb0ae12b3f85be6f290b936c4393484be31' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_buckets: {
          buckets: [
            {
              name: 'doc-example-bucket-1'
            },
            {
              name: 'doc-example-bucket-2'
            }
          ],
          owner: {
            display_name: grantee_display_name,
            id: grantee_id
          }
        },
        get_bucket_location: {
          location_constraint: region
        },
        get_bucket_acl: {
          owner: {
            display_name: grantee_display_name,
            id: grantee_id
          },
          grants: [
            {
              grantee: {
                display_name: grantee_display_name,
                id: grantee_id,
                type: 'CanonicalUser'
              },
              permission: 'READ'
            }
          ]
        }
      }
    )
  end

  it 'gets a list of buckets that are open for public read access' do
    open_buckets = get_open_buckets(s3_client, region)
    expect(open_buckets.count).to eq(0)
  end
end
