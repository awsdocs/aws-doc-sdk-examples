# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-get-item'

describe '#object_downloaded?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:local_path) { "./#{object_key}" }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_object: {}
      }
    )
  end

  it 'downloads an object' do
    expect(object_downloaded?(s3_client, bucket_name, object_key, local_path)).to be
  end
end
