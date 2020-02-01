# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[mullermp (AWS), pccornel (AWS)]
# snippet-sourcedescription:[Creates a CloudTrail trail.]
# snippet-keyword:[AWS CloudTrail]
# snippet-keyword:[create_trail method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[cloudtrail]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-28]
# Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
  # Initialize an instance of CreateTrailExample, creating clients for AWS STS,
  # AWS CloudTrail, and Amazon S3 (unless already provided 
  # during initialization).
  #
  # (The following comments express documentation about this function in YARD 
  # format by using @ symbols.)
  #
  # @param [Hash] opts ({}) A hash of API clients for S3, STS, and CloudTrail.
  # @option [Aws::S3::Client] :s3_client (Aws::S3::Client)
  # @option [Aws::STS::Client] :sts_client (Aws::STS::Client)
  # @option [Aws::CloudTrail::Client] :cloudtrail_client
  #  (Aws::CloudTrail::Client)
  def initialize(opts = {})
    # This S3 API client is used for :put_bucket_policy.
    @s3 = opts[:s3_client] || Aws::S3::Client.new
    # This STS API client is used to get the account ID.
    @sts = opts[:sts_client] || Aws::STS::Client.new
    # This CloudTrail API client is used to create the CloudTrail resource.
    @cloudtrail = opts[:cloudtrail_client] || Aws::CloudTrail::Client.new
  end
  
  # Creates the specified trail in CloudTrail.
  # Prerequisites:
  #  An existing S3 bucket with the name specified in bucket_name.
  #
  # @param trail_name [String] The name of the trail to create.
  # @param bucket_name [String] The bucket name to associate with the trail.
  # @param add_bucket_policy [Boolean] (false) Set to true to add a policy
  #  to the bucket if one does not already exist.
  def create_trail(trail_name, bucket_name, add_bucket_policy = false)
    if add_bucket_policy
      account_id = @sts.get_caller_identity.account
      @s3.put_bucket_policy(
        bucket: bucket_name,
        policy: define_policy(bucket_name, account_id)
      )
    end
  
    @cloudtrail.create_trail(
      name: trail_name,
      s3_bucket_name: bucket_name
    )
  rescue StandardError => e
    puts "Error in 'create_trail': #{e} (#{e.class})"
  end
  
  private
  
  # Defines an S3 bucket policy that is compatible with CloudTrail.
  # Used internally by create_trail.
  # Prerequisites:
  #  An existing bucket with the name specified in bucket_name.
  def define_policy(bucket_name, account_id)
    {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Sid' => 'AWSCloudTrailAclCheck20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com'
          },
          'Action' => 's3:GetBucketAcl',
          'Resource' => "arn:aws:s3:::#{bucket_name}"
        },
        {
          'Sid' => 'AWSCloudTrailWrite20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com'
          },
          'Action' => 's3:PutObject',
          'Resource' => "arn:aws:s3:::#{bucket_name}/AWSLogs/#{account_id}/*",
          'Condition' => {
            'StringEquals' => {
              's3:x-amz-acl' => 'bucket-owner-full-control'
            }
          }
        }
      ]
    }.to_json
  end
end
  
# Tests the functionality in the preceding class by using RSpec.
RSpec.describe CreateTrailExample do
  let(:trail_name)  { 'my-trail' }
  let(:bucket_name) { 'my-bucket' }
  
  let(:policy_json) do
    {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Sid' => 'AWSCloudTrailAclCheck20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com'
          },
          'Action' => 's3:GetBucketAcl',
          'Resource' => 'arn:aws:s3:::my-bucket'
        },
        {
          'Sid' => 'AWSCloudTrailWrite20150319',
          'Effect' => 'Allow',
          'Principal' => {
            'Service' => 'cloudtrail.amazonaws.com'
          },
          'Action' => 's3:PutObject',
          'Resource' => 'arn:aws:s3:::my-bucket/AWSLogs/123456789012/*',
          'Condition' => {
            'StringEquals' => {
              's3:x-amz-acl' => 'bucket-owner-full-control'
            }
          }
        }
      ]
    }.to_json
  end
  
  # Create stubbed API clients to use for testing.
  let(:sts_client)        { Aws::STS::Client.new(stub_responses: true) }
  let(:s3_client)         { Aws::S3::Client.new(stub_responses: true) }
  let(:cloudtrail_client) { Aws::CloudTrail::Client.new(stub_responses: true) }
  
  # Create a CreateTrailExample object with our API clients.
  let(:cloud_trail_example) do
    CreateTrailExample.new(
      sts_client: sts_client,
      s3_client: s3_client,
      cloudtrail_client: cloudtrail_client
    )
  end
  
  describe '#create_trail' do
    it 'creates an AWS CloudTrail trail' do
      expect_any_instance_of(Aws::CloudTrail::Client)
        .to receive(:create_trail).with(
          name: trail_name, s3_bucket_name: bucket_name
        )
      cloud_trail_example.create_trail(trail_name, bucket_name)
    end
  
    context 'add bucket policy is true' do
      it 'adds a policy to the bucket' do
        expect_any_instance_of(Aws::CloudTrail::Client)
          .to receive(:create_trail).with(
            name: trail_name, s3_bucket_name: bucket_name
          )
        expect_any_instance_of(Aws::S3::Client)
          .to receive(:put_bucket_policy).with(
            bucket: bucket_name,
            policy: policy_json
          )
  
        # Stub the response data for our STS API client.
        sts_client.stub_responses(
          :get_caller_identity, account: '123456789012'
        )
  
        cloud_trail_example.create_trail(trail_name, bucket_name, true)
      end
    end
  end
end