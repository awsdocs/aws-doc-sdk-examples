# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_request_object_keys.rb'
require_relative './s3_test_globals.rb'

describe 'Tests whether a set of objects in the specified Amazon S3 bucket can be listed' do
  it 'Lists the objects in the specified bucket' do
    expect(list_bucket_objects($s3_bucket_name, $s3_region_id)).to be
  end
end
