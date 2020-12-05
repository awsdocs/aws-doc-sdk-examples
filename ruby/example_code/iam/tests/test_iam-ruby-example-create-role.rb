# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-create-role'

describe '#create_role' do
  let(:role_name) { 'my-ec2-s3-dynamodb-full-access-role' }
  let(:assume_role_policy_document) do
    {
      Version: '2012-10-17',
      Statement: [
        {
          Effect: 'Allow',
          Principal: {
            Service: 'ec2.amazonaws.com'
          },
          Action: 'sts:AssumeRole'
        }
      ]
    }
  end
  let(:policy_arns) do
    [
      'arn:aws:iam::aws:policy/AmazonS3FullAccess',
      'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess'
    ]
  end
  let(:role_arn) { "arn:aws:iam::111111111111:role/#{role_name}" }
  let(:role_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_role: {
          role: {
            arn: role_arn,
            path: '/',
            role_name: role_name,
            create_date: Time.now,
            role_id: role_id
          }
        },
        attach_role_policy: {},
        get_role: {
          role: {
            arn: role_arn,
            path: '/',
            role_name: role_name,
            create_date: Time.now,
            role_id: role_id
          }
        }
      }
    )
  end

  it 'creates a role' do
    expect(
      create_role(
        iam_client,
        role_name,
        assume_role_policy_document,
        policy_arns
      )
    ).to eq(role_arn)
  end
end