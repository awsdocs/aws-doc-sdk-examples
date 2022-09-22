# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

# To run this demo, you will need Ruby 2.6 or later, plus dependencies.
# For more info, see:
# https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md

require "aws-sdk-lambda"
require "aws-sdk-cloudwatchlogs"
require "aws-sdk-iam"
require "logger"
require "json"
require "zip"
require "cli/ui"
require_relative("../../helpers/disclaimers")
require_relative("../../helpers/decorators")
require_relative("lambda_basics")
require_relative("lambda_function")

@logger = Logger.new($stdout)
@logger.level = Logger::WARN

# snippet-start:[ruby.example_code.lambda.Scenario_GettingStartedFunctions]

# Runs the scenario.
# @return [nil]
def run_scenario
  banner
  puts "######################################################################################################".yellow
  puts "#                                                                                                    #".yellow
  puts "#                                          EXAMPLE CODE DEMO:                                        #".yellow
  puts "#                                             AWS Lambda                                             #".yellow
  puts "#                                                                                                    #".yellow
  puts "######################################################################################################".yellow
  puts ""
  puts "You have launched a demo of AWS Lambda using the AWS for Ruby v3 SDK. Over the next 60 seconds, it will:"
  puts "    1. Create a basic IAM role and policy for Lambda invocation."
  puts "    2. Create a new Lambda function."
  puts "    3. Invoke the Lambda function."
  puts "    4. Update the Lambda function code."
  puts "    5. Update the Lambda function configuration."
  puts "    6. Destroy the Lambda function and associated IAM role."
  puts ""

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  source_file = "lambda_function"
  custom_name = "lambda-function-#{rand(10**4)}"
  wrapper = LambdaWrapper.new

  new_step(1, "Create IAM role and policy for Lambda")
  print "Creating..."
  role, policy = wrapper.manage_iam("#{custom_name}-role", "create")
  role_arn = role["role"]["arn"]
  print "Done!\n".green
  puts JSON.pretty_generate(role).green
  puts JSON.pretty_generate(policy).green
  puts "-" * 88

  new_step(2, "Create new Lambda function")
  print "Let's create a function code to increment a number by 1..."
  if wrapper.get_function(custom_name).nil?
    print "No function exists called #{custom_name}! \nLet's create a deployment package..."
    deployment_package = wrapper.create_deployment_package(source_file)
    print "Done!\n".green if deployment_package
    print "Uploading our deployment package to create the function..."
    response = wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role_arn, deployment_package)
    print "Done!\n".green
    puts JSON.pretty_generate(response).green
  end

  new_step(3, "List all functions")
  print "Let's do a quick list of all functions in our account..."
  response = wrapper.list_functions
  print "Done!\n".green
  puts "Discovered #{response.count} functions: #{response}"

  new_step(4, "Invoke the function")
  response = CLI::UI::Prompt.ask("Please provide a whole number to increment by 1:")
  print "Attempting to invoke #{custom_name}..."
  payload = { number: response }
  wrapper.invoke_and_verify(custom_name, "incremented", JSON.generate(payload))

  new_step(5, "Update function configuration")
  print "Let's enable debugging within our function configuration..."
  response = wrapper.update_function_configuration(custom_name, "debug")
  print "Done!\n".green if response
  puts JSON.pretty_generate(response).green
  print "Now let's invoke #{custom_name} again to see the impact of our changes..."
  wrapper.invoke_and_verify(custom_name, "This is a debug log message")

  new_step(6, "Update function code")
  print "Let's update our function code to multiply two whole numbers provided as invocation parameters..."
  new_deployment_package = wrapper.create_deployment_package("#{source_file}_updated")
  response = wrapper.update_function_code(custom_name, new_deployment_package)
  print "Done!\n".green if response
  puts JSON.pretty_generate(response).green
  print "Now let's invoke #{custom_name} again to see the impact of our changes...\n"
  first_number = CLI::UI::Prompt.ask("Please provide a first whole number:")
  second_number = CLI::UI::Prompt.ask("Please provide a second whole number:")
  payload = { first_number: first_number.to_i, second_number: second_number.to_i }
  wrapper.invoke_and_verify(custom_name, "product", JSON.generate(payload))

  new_step(7, "Delete function code")
  print "That's a wrap! Destroying IAM role and function..."
  wrapper.manage_iam("#{custom_name}-role", "destroy")
  print "Done!\n".green
  wrapper.delete_function(custom_name)
  print "Done!\n".green
  puts "==========================================================================".yellow
end

# snippet-end:[ruby.example_code.lambda.Scenario_GettingStartedFunctions]

run_scenario if __FILE__ == $PROGRAM_NAME
