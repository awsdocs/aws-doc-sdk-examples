# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-lambda'
require 'aws-sdk-cloudwatchlogs'
require 'aws-sdk-iam'
require 'logger'
require 'json'
require 'zip'
require_relative('lambda_basics')
require_relative('lambda_function')

@logger = Logger.new($stdout)
@logger.level = Logger::WARN

# snippet-start:[python.example_code.lambda.Scenario_GettingStartedFunctions]

# Runs the scenario.
# @param lambda_client: An SDK for Ruby v3 Lambda client.
# @param source_file: The name of the file that contains the basic Lambda handler.
# @param custom_name: The name to give resources created for the scenario
# @return [nil]
def run_scenario(lambda_client, source_file, custom_name)
  puts '-' * 88
  puts 'Welcome to the AWS Lambda getting started with functions demo.'
  puts '-' * 88

  wrapper = LambdaWrapper.new(lambda_client, source_file, custom_name)

  print 'Creating IAM role and policy for Lambda...'
  role_arn = wrapper.manage_iam("#{custom_name}-role", 'create')
  print "Done!\n"
  puts '-' * 88

  puts "Creating a new function: #{custom_name}..."
  if wrapper.get_function(custom_name).nil?
    deployment_package = wrapper.create_deployment_package(source_file)
    wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role_arn, deployment_package)
  end
  print "Done!\n"
  puts '-' * 88

  puts "Let's invoke #{custom_name}."
  response = wrapper.invoke_function(custom_name)
  if response
    wrapper.get_cloudwatch_logs(custom_name, 'Code executed successfully!')
  else
    puts 'Invocation failed.'
  end
  print "Done!\n"
  puts '-' * 88

  print "Let's update our function code!...\n"
  new_deployment_package = wrapper.create_deployment_package(source_file)
  response = wrapper.update_function_code(custom_name, new_deployment_package)
  puts "Done!" if response
  puts '-' * 88


  print "Let's enable debugging within our function configuration..."
  response = wrapper.update_function_configuration(custom_name, 'debug')
  print "Done!\n" if response
  puts "Now let's invoke #{custom_name} again to see the impact of our changes..."
  response = wrapper.invoke_function(custom_name)
  if response
    wrapper.get_cloudwatch_logs(custom_name, 'This is a debug log message')
  else
    puts 'Invocation failed.'
  end
  puts '-' * 88

  puts "That's a wrap! Destroying IAM role and function..."
  wrapper.manage_iam("#{custom_name}-role", 'destroy')
  print "Done!\n"
  puts '-' * 88
end

lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')
source_file = 'lambda_function'
custom_name = "lambda-function-#{rand(10**4)}"

run_scenario(lambda_client, source_file, custom_name) if __FILE__ == $PROGRAM_NAME
