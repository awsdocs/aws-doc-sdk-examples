# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_ruby_create_bucket'

describe '#list_buckets' do
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_buckets: {
          buckets: [
            {
              name: 'doc-example-bucket'
            },
            {
              name: 'doc-example-bucket1'
            }
          ]
        }
      }
    )
  end

  it 'lists the available buckets' do
    expect(list_buckets(s3_client)).not_to eq('No buckets.')
  end
end

describe '#bucket_created?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        create_bucket: {
          location: '/' + bucket_name
        }
      }
    )
  end

  it 'checks whether a bucket was created' do
    expect(bucket_created?(s3_client, bucket_name)).to be(true)
  end
end

describe '#object_uploaded?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:object_content) { 'This is the content of my-file.txt.' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {
          etag: "\"cf5a9aa4adf66b936d908f4acEXAMPLE\""
        }
      }
    )
  end

  it 'checks whether an object was uploaded' do
    expect(
      object_uploaded?(
        s3_client,
        bucket_name,
        object_key,
        object_content
      )
    ).to be(true)
  end
end

describe '#list_objects' do
  let(:object_key) { 'my-file.txt' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_objects_v2: {
          contents: [
            {
              key: object_key
            },
            {
              key: 'my-file-1.txt'
            }
          ]
        }
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }

  it 'lists the objects in the bucket' do
    objects = list_objects(
      s3_client,
      bucket_name
    )
    expect(list_objects(s3_client, bucket_name)).to be
  end
end

describe '#object_copied?' do
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        copy_object: {
          copy_object_result: {
            etag: "\"cf5a9aa4adf66b936d908f4acEXAMPLE\""
          }
        }
      }
    )
  end
  let(:source_bucket_name) { 'doc-example-bucket' }
  let(:source_object_key) { 'my-file.txt' }
  let(:target_bucket_name) { 'doc-example-bucket1' }
  let(:target_object_key) { 'my-file-1.txt' }

  it 'checks whether the object was copied' do
    expect(
      object_copied?(
        s3_client,
        source_bucket_name,
        source_object_key,
        target_bucket_name,
        target_object_key
      )
    ).to be(true)
  end
end

describe '#object_deleted?' do
  let(:object_key) { 'my-file.txt' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        delete_objects: {
          deleted: [
            {
              key: object_key
            }
          ]
        }
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }

  it 'checks whether an object was deleted' do
    expect(object_deleted?(s3_client, bucket_name, object_key)).to be(true)
  end
end
