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
require_relative('../../helpers/disclaimers')
require_relative('../../helpers/decorators')
require_relative('lambda_basics')
require_relative('lambda_function')

@logger = Logger.new($stdout)
@logger.level = Logger::WARN

# snippet-start:[ruby.example_code.lambda.Scenario_GettingStartedFunctions]

# Runs the scenario.
# @param lambda_client: An SDK for Ruby v3 Lambda client.
# @param source_file: The name of the file that contains the basic Lambda handler.
# @param custom_name: The name to give resources created for the scenario
# @return [nil]
def run_scenario(lambda_client, source_file, custom_name)
  banner
  puts "######################################################################################################".yellow
  puts "#                                                                                                    #".yellow
  puts "#                                          EXAMPLE CODE DEMO:                                        #".yellow
  puts "#                                             AWS Lambda                                             #".yellow
  puts "#                                                                                                    #".yellow
  puts "######################################################################################################".yellow
  puts ""
  puts 'You have launched a demo of AWS Lambda using the AWS for Ruby v3 SDK. Over the next 60 seconds, it will:'
  puts '    1. create a basic IAM role and policy for Lambda invocation.'
  puts '    2. create a new Lambda function'
  puts '    3. invoke the Lambda function'
  puts '    4. update the Lambda function code'
  puts '    5. update the Lambda function configuration'
  puts '    6. destroy the Lambda function and associated IAM role'
  puts ''

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  wrapper = LambdaWrapper.new(lambda_client, source_file, custom_name)

  new_step(1, 'Create IAM role and policy for Lambda')
  print 'Creating...'
  response = wrapper.manage_iam("#{custom_name}-role", 'create')
  role_arn = response["role"]["arn"]
  print "Done!\n".green
  puts JSON.pretty_generate(response).green
  puts '-' * 88

  new_step(2, "Create new Lambda function")
  if wrapper.get_function(custom_name).nil?
    print "No function exists called #{custom_name}. Let's create a deployment package..."
    deployment_package = wrapper.create_deployment_package(source_file)
    print "Done!\n".green if deployment_package
    print "Uploading our deployment package to create the function..."
    wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role_arn, deployment_package)
  end

  new_step(3, "Invoke the function")
  print "Attempting to invoke #{custom_name}..."
  response = wrapper.invoke_function(custom_name)
  print "Success!\n".green
  if response
    wrapper.get_cloudwatch_logs(custom_name, 'INFO')
  else
    puts 'Invocation failed.'
  end

  new_step(4, "Update function code")
  new_deployment_package = wrapper.create_deployment_package(source_file)
  response = wrapper.update_function_code(custom_name, new_deployment_package)
  puts "Done!" if response
  puts JSON.pretty_generate(response).green

  new_step(5, "Update function configuration")
  print "Let's enable debugging within our function configuration..."
  response = wrapper.update_function_configuration(custom_name, 'debug')
  print "Done!\n".green if response
  puts "Now let's invoke #{custom_name} again to see the impact of our changes..."
  response = wrapper.invoke_function(custom_name)
  if response
    wrapper.get_cloudwatch_logs(custom_name, 'This is a debug log message')
  else
    puts 'Invocation failed.'
  end
  puts JSON.pretty_generate(response).green

  new_step(6, "Delete function code")
  print "That's a wrap! Destroying IAM role and function..."
  wrapper.manage_iam("#{custom_name}-role", 'destroy')
  print "Done!\n".green
  puts "==========================================================================".yellow
end

lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')
source_file = 'lambda_function'
custom_name = "lambda-function-#{rand(10**4)}"

run_scenario(lambda_client, source_file, custom_name) if __FILE__ == $PROGRAM_NAME
