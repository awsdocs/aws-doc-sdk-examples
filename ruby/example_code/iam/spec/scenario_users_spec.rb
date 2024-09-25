# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'rspec'
require_relative '../scenario_users'

describe 'create user assume role scenario' do
  context 'runs against AWS (integration tests)', integ: true do
    it 'runs without errors', quarantine: true do
      iam_client = Aws::IAM::Client.new
      scenario = ScenarioCreateUserAssumeRole.new(iam_client)

      expect { run_scenario(scenario) }.not_to output('/Something went wrong with the demo/').to_stdout
    end
  end
end
