# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_federation_token_request_test.rb'
require_relative './s3_test_globals.rb'

describe 'Tests whether an AWS IAM user with limited permissions can list objects in an Amazon S3 bucket' do
  it 'Finds or creates the specified user' do
    expect(get_user($s3_region_id, $iam_user_name)).to be
  end
  it 'Uses the user to list the objects in the specified bucket' do
    expect(list_objects_with_temporary_credentials($s3_region_id, $iam_user_name, $s3_bucket_name)).to be
  end
end
