# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../scenario_getting_started"

describe "getting started scenario" do
  after do
    File.delete("download.test") if File.exist?("download.test")
  end

  context "runs against AWS (integration tests)", integ: true do

    it "runs without errors", integ: true do
      s3_resource = Aws::S3::Resource.new
      scenario = ScenarioGettingStarted.new(s3_resource)
      allow(scenario).to receive(:gets).and_return(
        __FILE__, "y", "download.test", "y", "y"
      )

      expect { run_scenario(scenario) }.not_to output("Something went wrong with the demo!").to_stdout
    end
  end

  context "runs using stubs" do
    let(:s3_resource) { Aws::S3::Resource.new(stub_responses: true) }
    let(:scenario) { ScenarioGettingStarted.new(s3_resource) }
    inputs = [__FILE__, "y", "download.test", "y", "y"]

    it "runs without errors" do
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.not_to output("Something went wrong with the demo!").to_stdout
    end

    it "outputs correct error when create_bucket fails" do
      s3_resource.client.stub_responses(:create_bucket, "TestError")
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when upload_file fails" do
      s3_resource.client.stub_responses(:put_object, "TestError")
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when copy_object fails" do
      s3_resource.client.stub_responses(:copy_object, "TestError")
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when list_objects fails" do
      s3_resource.client.stub_responses(:create_bucket, "TestError")
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_bucket fails" do
      s3_resource.client.stub_responses(:create_bucket, "TestError")
      allow(scenario).to receive(:gets).and_return(*inputs)
      expect { run_scenario(scenario) }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end
  end
end
