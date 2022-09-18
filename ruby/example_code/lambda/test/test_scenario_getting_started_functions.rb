# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# To run this demo, you will need Ruby 2.6 or later, plus dependencies.
# For more info, see:
# https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md

require 'aws-sdk-lambda'
require 'aws-sdk-cloudwatchlogs'
require 'aws-sdk-iam'
require 'logger'
require 'json'
require 'zip'
require 'cli/ui'
require_relative('../../../helpers/disclaimers')
require_relative('../../../helpers/decorators')
require_relative('../lambda_basics')
require_relative('../lambda_function')
require 'rspec'

describe LambdaWrapper do
  context 'LambdaWrapper' do
    custom_name = "lambda-function-#{rand(10**4)}"
    source_file = 'lambda_function'
    wrapper = LambdaWrapper.new
    role, policy = wrapper.manage_iam("#{custom_name}-role", 'create')

    it 'creates IAM role and attaches policy' do
      expect(role["role"]["role_name"]).to eq("#{custom_name}-role")
      expect(role["role"]["assume_role_policy_document"].to_s).to be_instance_of(String)
    end

    it 'creates a new deployment package' do
      deployment_package = wrapper.create_deployment_package(source_file)
      expect(deployment_package).to be_instance_of(String)
    end

    it 'creates new Lambda function' do
      deployment_package = wrapper.create_deployment_package(source_file)
      response = wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role["role"]["arn"], deployment_package)
      expect(response["function_name"]).to start_with(custom_name)
    end

    it 'lists all Lambda functions' do
      response = wrapper.list_functions
      # expect(response).to be true # and have a count of 1 in it
      expect(response.find { |item| item == custom_name }).to_not be_nil
    end

    it 'invokes the Lambda function' do
      payload = { :number => '2' }
      log_statement = wrapper.invoke_and_verify(custom_name, 'incremented', JSON.generate(payload))
      expect(log_statement).to include('You provided 2 and it was incremented to 3')
    end

    it 'updates the Lambda function configuration' do
      response = wrapper.update_function_configuration(custom_name, 'debug')
      payload = { :number => '2' }
      log_statement = wrapper.invoke_and_verify(custom_name, 'debug', JSON.generate(payload))
      puts log_statement
      expect(response).to be_truthy
      expect(log_statement.to_s).to include('This is a debug log message')
    end

    it 'updates Lambda function code' do
      new_deployment_package = wrapper.create_deployment_package("#{source_file}_updated")
      response = wrapper.update_function_code(custom_name, new_deployment_package)
      payload = { :first_number => 4, :second_number => 4 }
      log_statement = wrapper.invoke_and_verify(custom_name, 'product', JSON.generate(payload))
      expect(response).to be_truthy
      expect(log_statement).to include('The product of 4.0 and 4.0 is 16')
    end

    it 'deletes Lambda function' do
      wrapper.delete_function(custom_name)
    end

    it 'deletes IAM role' do
      wrapper.manage_iam("#{custom_name}-role", 'destroy')
    end
  end
end

