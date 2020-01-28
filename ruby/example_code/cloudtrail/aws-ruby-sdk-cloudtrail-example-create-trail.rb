# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates a CloudTrail trail.]
# snippet-keyword:[AWS CloudTrail]
# snippet-keyword:[create_trail method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[cloudtrail]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-cloudtrail'
require 'aws-sdk-s3'
require 'aws-sdk-sts'

# Creates a trail in AWS CloudTrail.
class CreateTrailExample
  # Creates the specified trail in AWS CloudTrail.
  # Prerequisites:
  #  An existing Amazon S3 bucket with the name specified in bucket_name.
  # Inputs:
  #  cloudtrail_client: an instance of an AWS CloudTrail API client.
  #  s3_client: an instance of an Amazon S3 API client.
  #  trail_name: the name of the trail to create.
  #  region_id: the ID of the AWS Region to create the trail in.
  #  bucket_name: the name of the bucket to associate with the trail.
  #  account_id: the ID of the AWS account to create the trail in.
  #  add_bucket_policy: true to add the specified policy to the bucket.
  # Outputs:
  #  Nothing.
  def create_trail(cloudtrail_client, s3_client, trail_name, region_id, bucket_name, account_id, add_bucket_policy)
    
    if add_bucket_policy == true
      policy = define_policy(region_id, bucket_name, account_id)
      add_policy_to_bucket(s3_client, region_id, policy, bucket_name)
    end

    begin
      cloudtrail_client.create_trail({
        name: trail_name,
        s3_bucket_name: bucket_name
      })
    rescue Exception => ex
      puts "Error in 'create_trail': #{ex} (#{ex.class})"
    end
  end

  # Defines an Amazon S3 bucket policy that is compatible with AWS CloudTrail.
  # Prerequisites:
  #  An existing bucket with the name specified in bucket_name.
  # Inputs:
  #  region_id: the ID of the AWS Region for the associated AWS CloudTrail resources.
  #  bucket_name: the name of the bucket to associate with the policy.
  #  account_id: the ID of the AWS account to associate with the policy.
  # Outputs:
  #  The bucket policy in JSON format.
  def define_policy(region_id, bucket_name, account_id)
    policy = {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Sid' => 'AWSCloudTrailAclCheck20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:GetBucketAcl',
          'Resource' => 'arn:aws:s3:::' + bucket_name,
        },
        {
          'Sid' => 'AWSCloudTrailWrite20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:PutObject',
          'Resource' => 'arn:aws:s3:::' + bucket_name + '/AWSLogs/' + account_id + '/*',
          'Condition' => {
            'StringEquals' => {
              's3:x-amz-acl' => 'bucket-owner-full-control',
            },
          },
        },
      ]
    }.to_json
    return policy
  end

  # Adds the specified Amazon S3 bucket policy to the specified bucket.
  # Prerequisites:
  #  An existing Amazon S3 bucket policy with the contents specified in policy.
  #  An existing bucket with the name specified in bucket_name.
  # Inputs:
  #  s3_client: an instance of an Amazon S3 API client.
  #  region_id: the ID of the AWS Region for the associated bucket.
  #  policy: the JSON-formatted bucket policy.
  #  bucket_name: the name of the bucket to associate with the policy.
  # Outputs:
  #  Nothing.
  def add_policy_to_bucket(s3_client, region_id, policy, bucket_name)

    begin
      s3_client.put_bucket_policy(
        bucket: bucket_name,
        policy: policy
      )
    rescue Exception => ex
      puts "Error in 'add_policy_to_bucket': #{ex} (#{ex.class})"
    end
  end
end

# Tests the functionality in the preceding class by using RSpec.
RSpec.describe CreateTrailExample do

  region_id = 'us-east-1'
  account_id = ''
  bucket_name = 'my-test-bucket'
  trail_name = ''
  cloudtrail_client = ''
  mock_cloudtrail_client = ''
  s3_client = ''
  mock_s3_client = ''

  before(:all) do
    # Get account ID using AWS STS.
    sts_client = Aws::STS::Client.new(region: region_id)
    sts_resp = sts_client.get_caller_identity({})
    account_id = sts_resp.account
    # Unique name of trail to create in AWS CloudTrail.
    trail_name = 'my-trail-' + account_id + '-' + Time.now.to_i.to_s
    # Get a new AWS CloudTrail API client.
    cloudtrail_client = Aws::CloudTrail::Client.new(region: region_id)
    # Get a new mock AWS CloudTrail API client.
    mock_cloudtrail_client = Aws::CloudTrail::Client.new(stub_responses: true)
    # Get a new Amazon S3 API client.
    s3_client = Aws::S3::Client.new(region: region_id)
    # Get a new mock Amazon S3 API client.
    mock_s3_client = Aws::S3::Client.new(stub_responses: true)
  end

  it 'actually defines an Amazon S3 bucket policy' do
    policy = CreateTrailExample.new.define_policy(region_id, bucket_name, account_id)
    expect(policy).to be
    puts policy
  end

  # To run all mocks only: rspec aws-ruby-sdk-cloudtrail-example-create-trail.rb -E 'mocks*' -f d
  it 'mocks defining an Amazon S3 bucket policy' do
    policy = instance_double("CreateTrailExample")
    allow(policy).to receive(:define_policy).with(region_id, bucket_name, account_id).and_return({
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Sid' => 'AWSCloudTrailAclCheck20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:GetBucketAcl',
          'Resource' => 'arn:aws:s3:::us-east-1',
        },
        {
          'Sid' => 'AWSCloudTrailWrite20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:PutObject',
          'Resource' => 'arn:aws:s3:::my-test-bucket/AWSLogs/123456789012/*',
          'Condition' => {
            'StringEquals' => {
              's3:x-amz-acl' => 'bucket-owner-full-control',
            },
          },
        },
      ]
    })

    expect(policy.define_policy(region_id, bucket_name, account_id)).to eq({
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Sid' => 'AWSCloudTrailAclCheck20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:GetBucketAcl',
          'Resource' => 'arn:aws:s3:::us-east-1',
        },
        {
          'Sid' => 'AWSCloudTrailWrite20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com',
          },
          'Action' => 's3:PutObject',
          'Resource' => 'arn:aws:s3:::my-test-bucket/AWSLogs/123456789012/*',
          'Condition' => {
            'StringEquals' => {
              's3:x-amz-acl' => 'bucket-owner-full-control',
            },
          },
        },
      ]
    })
  end

  # Note: Running this example might result in changes and additional charges to your AWS account.
  it 'actually adds a policy to a bucket' do
    create_trail = CreateTrailExample.new
    policy = create_trail.define_policy(region_id, bucket_name, account_id)
    expect(create_trail.add_policy_to_bucket(s3_client, region_id, policy, bucket_name)).to be
  end

  it 'mocks adding a policy to a bucket' do
    create_trail = CreateTrailExample.new
    policy = create_trail.define_policy(region_id, bucket_name, account_id)
    expect(create_trail.add_policy_to_bucket(mock_s3_client, region_id, policy, bucket_name)).to be
  end

  # Note: Running this example might result in changes and additional charges to your AWS account.
  it 'actually creates an AWS CloudTrail trail' do
    expect(CreateTrailExample.new.create_trail(cloudtrail_client, s3_client, trail_name, region_id, bucket_name, account_id, true)).to be
  end

  it 'mocks creating an AWS CloudTrail trail' do
    expect(CreateTrailExample.new.create_trail(mock_cloudtrail_client, s3_client, trail_name, region_id, bucket_name, account_id, true)).to be
  end

end
