# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-set-item-props'

describe '#object_copied_with_additional_properties?' do
  let(:source_object_path) { 'doc-example-bucket/my-file.txt' }
  let(:target_bucket_name) { 'doc-example-bucket1' }
  let(:target_object_path) { 'copied-files/my-copied-file.txt' }
  let(:canned_acl) { 'bucket-owner-read' }
  let(:storage_class) { 'STANDARD_IA' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        copy_object: {
          copy_object_result: {
            etag: "\"6805f2cfc46c0f04559748bb039d69ae\""
          }
        }
      }
    )
  end

  it 'copies the object with additional properties' do
    expect(
      object_copied_with_additional_properties?(
        s3_client,
        source_object_path,
        target_bucket_name,
        target_object_path,
        canned_acl,
        storage_class
      )
    ).to be(true)
  end
end
