require_relative "../scenario_users"
require "aws-sdk-iam"
require "securerandom"

describe IAMUserAndRoleManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:logger) { Logger.new($stdout) }
  let(:user_and_role_manager) { IAMUserAndRoleManager.new(iam_client, logger) }
  let(:user_name) { "rspec-user-#{SecureRandom.uuid}" }
  let(:role_name) { "rspec-role-#{SecureRandom.uuid}" }

  after(:each) do
    begin
      user_and_role_manager.delete_user(user_name)
    rescue Aws::IAM::Errors::NoSuchEntity
      # User already deleted or never existed, safe to ignore
    end

    begin
      user_and_role_manager.delete_role(role_name)
    rescue Aws::IAM::Errors::NoSuchEntity
      # Role already deleted or never existed, safe to ignore
    end
  end

  it "creates and manages an IAM user and role" do
    user = user_and_role_manager.create_user(user_name)
    expect(user.user_name).to eq(user_name)

    role = user_and_role_manager.create_role(role_name, user.arn)
    expect(role.role_name).to eq(role_name)

    # Additional tests for access keys, policies, and assume role functionality can be added here

    user_and_role_manager.delete_user(user_name)
    user_and_role_manager.delete_role(role_name)

    # Verify deletion or use AWS SDK to confirm resources no longer exist
  end
end
