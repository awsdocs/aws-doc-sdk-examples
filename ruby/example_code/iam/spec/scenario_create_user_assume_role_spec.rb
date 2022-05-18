# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../scenario_create_user_assume_role"

describe "create user assume role scenario" do
  context "runs against AWS (integration tests)", integ: true do

    it "runs without errors", integ: true do
      iam_resource = Aws::IAM::Resource.new
      scenario = ScenarioCreateUserAssumeRole.new(iam_resource)

      expect { run_scenario(scenario) }.not_to output("/Something went wrong with the demo/").to_stdout
    end
  end

  context "runs using stubs" do
    let(:iam_resource) { Aws::IAM::Resource.new(stub_responses: true) }
    let(:s3_resource_no_perms) {
      resource = Aws::S3::Resource.new(stub_responses: true)
      resource.client.stub_responses(:list_buckets, "AccessDenied")
      resource
    }
    let(:s3_resource_assumed_role) { Aws::S3::Resource.new(stub_responses: true) }
    let(:sts_client) { Aws::STS::Client.new(stub_responses: true) }
    let(:scenario) { ScenarioCreateUserAssumeRole.new(iam_resource) }

    before do
      allow(scenario).to receive(:sleep)
      allow(scenario).to receive(:create_s3_resource).and_return(
        s3_resource_no_perms, s3_resource_assumed_role)
      allow(scenario).to receive(:create_sts_client).and_return(sts_client)
    end

    it "runs without errors" do
      iam_resource.client.stub_responses(:list_attached_role_policies, {
        'attached_policies': [{'policy_name': "test-policy", 'policy_arn': "test-arn"}]
      })
      iam_resource.client.stub_responses(:list_user_policies, {
        'policy_names': ["test-user-policy"]
      })
      iam_resource.client.stub_responses(:list_access_keys, {
        'access_key_metadata': [{'access_key_id': "test-key-id"}]
      })
      expectation = expect { run_scenario(scenario) }
      expectation.not_to output(/Something went wrong with the demo/).to_stdout
      expectation.to output(/Deleted policy/).to_stdout
      expectation.to output(/Deleted user policy test-user-policy/).to_stdout
      expectation.to output(/Deleted access key/).to_stdout
    end

    it "outputs correct error when create_user fails" do
      iam_resource.client.stub_responses(:create_user, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when create_access_key_pair fails" do
      iam_resource.client.stub_responses(:create_access_key, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when create_role fails" do
      iam_resource.client.stub_responses(:create_role, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when create_policy fails" do
      iam_resource.client.stub_responses(:create_policy, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when create_user_policy fails" do
      iam_resource.client.stub_responses(:put_user_policy, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_buckets fails" do
      s3_resource_assumed_role.client.stub_responses(:list_buckets, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_role fails" do
      iam_resource.client.stub_responses(:delete_role, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_user fails" do
      iam_resource.client.stub_responses(:delete_user, "TestError")
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end
  end
end
