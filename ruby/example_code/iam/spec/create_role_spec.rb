require "aws-sdk-iam"
require_relative("../manage_roles")
require "rspec"

describe RoleManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:role_manager) { RoleManager.new(iam_client) }
  let(:role_name) { "test-role-#{Time.now.to_i}" }
  let(:assume_role_policy_document) {
    {
      Version: "2012-10-17",
      Statement: [
        {
          Effect: "Allow",
          Principal: { Service: "ec2.amazonaws.com" },
          Action: "sts:AssumeRole"
        }
      ]
    }
  }
  let(:policy_arns) {
    [
      "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
    ]
  }

  after(:each) do
    # Detach policies and delete role for cleanup
    policy_arns.each do |policy_arn|
      iam_client.detach_role_policy(role_name: role_name, policy_arn: policy_arn)
    rescue Aws::IAM::Errors::NoSuchEntity
      # Ignore if the entity or policy does not exist
    end
    iam_client.delete_role(role_name: role_name)
  rescue Aws::IAM::Errors::NoSuchEntity
    # Ignore if the role does not exist
  end

  it "creates a role and attaches policies" do
    role_arn = role_manager.create_role(role_name, assume_role_policy_document, policy_arns)
    expect(role_arn).not_to be_nil

    # Verify role exists
    expect { iam_client.get_role(role_name: role_name) }.not_to raise_error
  end
end
