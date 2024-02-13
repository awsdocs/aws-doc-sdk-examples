require "aws-sdk-iam"
require_relative("../manage_access_keys")
require "rspec"

describe AccessKeyManager do
  let(:iam) { Aws::IAM::Client.new }
  let(:manager) { AccessKeyManager.new(iam) }
  let(:user_name) { "test-user-#{Time.now.to_i}" }

  before(:all) do
    iam.create_user(user_name: user_name)
  end

  after(:all) do
    iam.delete_user(user_name: user_name)
  end

  it "creates and deletes an access key for a user" do
    access_key = manager.create_access_key(user_name)
    expect(access_key).not_to be_nil

    keys_after_creation = manager.list_access_keys(user_name)
    expect(keys_after_creation).to include(access_key.access_key_id)

    delete_success = manager.delete_access_key(user_name, access_key.access_key_id)
    expect(delete_success).to be true

    keys_after_deletion = manager.list_access_keys(user_name)
    expect(keys_after_deletion).not_to include(access_key.access_key_id)
  end
end
