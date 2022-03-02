# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../iam_wrapper"

describe "iam_wrapper usage demo" do
  inputs = %w(1 1 batch.amazonaws.com y)

  context "runs against AWS (integration tests)", integ: true do
    it "runs without errors", integ: true do
      iam_resource = Aws::IAM::Resource.new
      wrapper = IamWrapper.new(iam_resource)
      allow(wrapper).to receive(:gets).and_return(*inputs)

      expect { usage_demo(wrapper) }.not_to output(/Something went wrong with the demo/).to_stdout
    end
  end

  context "runs using stubs" do
    role_name = "test-role"
    policy_arn = "test-policy-arn"
    group_name = "test-group"
    saml_provider_arn = "test-saml-provider-arn"
    let(:iam_resource) { Aws::IAM::Resource.new(stub_responses: true) }
    let(:wrapper) { IamWrapper.new(iam_resource) }

    before do
      allow(wrapper).to receive(:sleep)
      allow(wrapper).to receive(:gets).and_return(*inputs)
      iam_resource.client.stub_responses(:list_roles, {
        'roles': [{
                    'role_name': role_name, 'path': "test-path", 'role_id': "test-id",
                    'arn': "test-arn", 'create_date': Time.now}]
      })
      iam_resource.client.stub_responses(:list_policies, {
        'policies': [{'arn': policy_arn}]
      })
      iam_resource.client.stub_responses(:list_groups, {
        'groups': [{
                     'group_name': group_name, 'path': "test-path", 'group_id': "test-id",
                     'arn': "test-arn", 'create_date': Time.now}]
      })
      iam_resource.client.stub_responses(:list_saml_providers, {
        'saml_provider_list': [{'arn': saml_provider_arn}]
      })
      iam_resource.client.stub_responses(:get_service_linked_role_deletion_status, {
        'status': "SUCCEEDED"
      })
    end

    it "runs without errors" do
      expectation = expect { usage_demo(wrapper) }
      expectation.not_to output(/Something went wrong with the demo/).to_stdout
      expectation.to output(/#{role_name}/).to_stdout
      expectation.to output(/#{policy_arn}/).to_stdout
      expectation.to output(/#{group_name}/).to_stdout
      expectation.to output(/#{saml_provider_arn}/).to_stdout
      expectation.to output(/SUCCEEDED/).to_stdout
    end

    it "outputs correct error when list_roles fails" do
      iam_resource.client.stub_responses(:list_roles, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when get_role fails" do
      iam_resource.client.stub_responses(:get_role, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_users fails" do
      iam_resource.client.stub_responses(:list_users, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_policies fails" do
      iam_resource.client.stub_responses(:list_policies, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when get_policy fails" do
      iam_resource.client.stub_responses(:get_policy, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_groups fails" do
      iam_resource.client.stub_responses(:list_groups, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when get_account_password_policy fails" do
      iam_resource.client.stub_responses(:get_account_password_policy, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_saml_providers fails" do
      iam_resource.client.stub_responses(:list_saml_providers, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when create_service_linked_role fails" do
      iam_resource.client.stub_responses(:create_service_linked_role, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_service_linked_role fails" do
      iam_resource.client.stub_responses(:delete_service_linked_role, "TestError")
      expect { usage_demo(wrapper) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end
  end
end
