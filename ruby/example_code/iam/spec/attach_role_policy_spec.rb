require "aws-sdk-iam"
require_relative("../attach_user_policy")
require "rspec"

describe RolePolicyManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:user_manager) { RolePolicyManager.new(iam_client) }
  let(:user_name) { "test-user-#{Time.now.to_i}" }
  let(:policy_arn) { "arn:aws:iam::aws:policy/AmazonS3FullAccess" }

  before(:all) do
    # Setup test user
    iam_client.create_user(user_name: user_name)
  end

  after(:all) do
    # Cleanup test user
    iam_client.delete_user(user_name: user_name)
  end

  it "attaches a policy to a user" do
    expect(user_manager.attach_policy_to_user(user_name, policy_arn)).to be true
  end
end
