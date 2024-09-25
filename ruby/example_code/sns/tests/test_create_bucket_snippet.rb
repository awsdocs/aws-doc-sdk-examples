# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../create_bucket_snippet'

describe '#bucket_created?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
<<<<<<< HEAD
  let(:bucket_name) { "amzn-s3-demo-bucket" }
=======
  let(:bucket_name) { 'doc-example-bucket' }
>>>>>>> 999c6133e (fixes)

  it 'confirms the bucket was created' do
    bucket_data = s3_client.stub_data(
      :create_bucket,
      {
        location: 'us-west-2'
      }
    )
    s3_client.stub_responses(:create_bucket, bucket_data)
    expect(bucket_created?(s3_client, bucket_name).location).to eq('us-west-2')
  end
end
