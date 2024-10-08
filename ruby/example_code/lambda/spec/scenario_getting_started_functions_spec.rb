# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require 'json'
require 'rspec'
require_relative '../../../helpers/disclaimers'
require_relative '../../../helpers/decorators'
require_relative '../lambda_basics'
require_relative '../lambda_function'

describe LambdaWrapper do
  context 'LambdaWrapper' do
    custom_name = "lambda-function-#{rand(10**4)}"
    source_file = 'lambda_function'
    wrapper = LambdaWrapper.new
    # role, policy = wrapper.manage_iam("#{custom_name}-role", "create")

    it 'creates IAM role and attaches policy', integ: false do
      expect(role['role']['role_name']).to eq("#{custom_name}-role")
      expect(role['role']['assume_role_policy_document'].to_s).to be_instance_of(String)
    end

    it 'creates a new deployment package', integ: false do
      deployment_package = wrapper.create_deployment_package(source_file)
      expect(deployment_package).to be_instance_of(String)
    end

    it 'creates new Lambda function', integ: false do
      deployment_package = wrapper.create_deployment_package(source_file)
      response = wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role['role']['arn'],
                                         deployment_package)
      expect(response['function_name']).to start_with(custom_name)
    end

    it 'lists all Lambda functions', integ: false do
      response = wrapper.list_functions
      expect(response.find { |item| item == custom_name }).to_not be_nil
    end

    it 'invokes the Lambda function', integ: false do
      payload = { number: '2' }
      log_statement = wrapper.invoke_and_verify(custom_name, 'incremented', JSON.generate(payload))
      expect(log_statement).to include('You provided 2 and it was incremented to 3')
    end

    it 'updates the Lambda function configuration', integ: false do
      response = wrapper.update_function_configuration(custom_name, 'debug')
      payload = { number: '2' }
      log_statement = wrapper.invoke_and_verify(custom_name, 'debug', JSON.generate(payload))
      puts log_statement
      expect(response).to be_truthy
      expect(log_statement.to_s).to include('This is a debug log message')
    end

    it 'updates Lambda function code', integ: false do
      new_deployment_package = wrapper.create_deployment_package("#{source_file}_updated")
      response = wrapper.update_function_code(custom_name, new_deployment_package)
      payload = { first_number: 4, second_number: 4 }
      log_statement = wrapper.invoke_and_verify(custom_name, 'product', JSON.generate(payload))
      expect(response).to be_truthy
      expect(log_statement).to include('The product of 4 and 4 is 16')
    end

    it 'deletes Lambda function', integ: false do
      wrapper.delete_function(custom_name)
    end

    it 'deletes IAM role', integ: false do
      wrapper.manage_iam("#{custom_name}-role", 'destroy')
    end
  end
end
