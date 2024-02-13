require "aws-sdk-iam"
require "rspec"
require_relative "../manage_roles"

describe RoleManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:role_manager) { RoleManager.new(iam_client) }

  let(:role_name) { "rspec-test-role-#{Time.now.to_i}" }
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
  let(:policy_arns) { ["arn:aws:iam::aws:policy/ReadOnlyAccess"] }

  before(:each) do
    role_manager.create_role(role_name, assume_role_policy_document, policy_arns)
  end

  after(:each) do
    role_manager.delete_role(role_name) rescue nil # Clean up role
  end

  describe "#create_role" do
    it "creates a role and returns its ARN" do
      role_arn = role_manager.create_role(role_name, assume_role_policy_document, policy_arns)
      expect(role_arn).to be_a(String)
      expect(role_arn).to include("arn:aws:iam::")
    end
  end

  describe "#list_roles" do
    it "lists available roles" do
      roles = role_manager.list_roles(100) # Adjust count as necessary
      expect(roles).to include(role_name)
    end
  end

  describe "#get_role" do
    it "retrieves data about a specific role" do
      role = role_manager.get_role(role_name)
      expect(role).to be_a(Aws::IAM::Role)
      expect(role.role_name).to eq(role_name)
    end
  end

  describe "#delete_role" do
    it "deletes a specified role" do
      expect { role_manager.delete_role(role_name) }.not_to raise_error
    end
  end
end
