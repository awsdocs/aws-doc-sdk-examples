# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative('../attach_role_policy')
require 'rspec'

describe RolePolicyManager do
  before(:all) do
    @iam_client = Aws::IAM::Client.new
    @manager = RolePolicyManager.new(@iam_client)
    @role_name = "test-role-#{Time.now.to_i}"
    @policy_name = "test-policy-#{Time.now.to_i}"
    @policy_document = {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Effect' => 'Allow',
          'Action' => 's3:ListAllMyBuckets',
          'Resource' => 'arn:aws:s3:::*'
        }
      ]
    }
    @iam_client.create_role(
      role_name: @role_name,
      assume_role_policy_document: {
        'Version' => '2012-10-17',
        'Statement' => [
          {
            'Effect' => 'Allow',
            'Principal' => { 'Service' => 'ec2.amazonaws.com' },
            'Action' => 'sts:AssumeRole'
          }
        ]
      }.to_json
    )
  end

  after(:all) do
    @iam_client.delete_role(role_name: @role_name)
  end

  it 'creates, attaches, lists, and detaches a policy to a role' do
    policy_arn = @manager.create_policy(@policy_name, @policy_document)
    expect(policy_arn).not_to be_nil

    attach_success = @manager.attach_policy_to_role(@role_name, policy_arn)
    expect(attach_success).to be true

    attached_policies = @manager.list_attached_policy_arns(@role_name)
    expect(attached_policies).to include(policy_arn)

    detach_success = @manager.detach_policy_from_role(@role_name, policy_arn)
    expect(detach_success).to be true
  end
end
