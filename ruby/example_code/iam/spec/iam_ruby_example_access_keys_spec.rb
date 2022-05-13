# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../iam_ruby_example_access_keys"

describe "#list_access_keys(iam, user_name)" do
  let(:user_name) { "my-user" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_access_keys: {
          access_key_metadata: [
            {
              access_key_id: "AKIA111111111EXAMPLE"
            }
          ]
        }
      }
    )
  end

  it "lists information about available access keys" do
    expect { list_access_keys(iam_client, user_name) }.not_to raise_error
  end
end

describe "#create_access_key" do
  let(:user_name) { "my-user" }
  let(:access_key_id) { "AKIAIOSFODNN7EXAMPLE" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_access_key: {
          access_key: {
            access_key_id: access_key_id,
            secret_access_key: "wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY",
            status: "Active",
            user_name: user_name
          }
        }
      }
    )
  end

  it "creates an access key" do
    expect(create_access_key(iam_client, user_name).access_key_id).to eq(access_key_id)
  end
end

describe "#access_keys_last_used" do
  let(:user_name) { "my-user" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_access_keys: {
          access_key_metadata: [
            {
              access_key_id: "AKIA111111111EXAMPLE"
            }
          ]
        },
        get_access_key_last_used: {
          access_key_last_used: {
            last_used_date: Time.now,
            service_name: "N/A",
            region: "N/A"
          }
        }
      }
    )
  end

  it "lists information about when access keys were last used" do
    expect { access_keys_last_used(iam_client, user_name) }.not_to raise_error
  end
end

describe "#access_key_deactivated?" do
  let(:user_name) { "my-user" }
  let(:access_key_id) { "AKIA111111111EXAMPLE" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        update_access_key: {}
      }
    )
  end

  it "deactivates an access key" do
    expect(access_key_deactivated?(iam_client, user_name, access_key_id)).to be(true)
  end
end

describe "#access_key_deleted?" do
  let(:user_name) { "my-user" }
  let(:access_key_id) { "AKIA111111111EXAMPLE" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        delete_access_key: {}
      }
    )
  end

  it "deletes an access key" do
    expect(access_key_deleted?(iam_client, user_name, access_key_id)).to be(true)
  end
end
