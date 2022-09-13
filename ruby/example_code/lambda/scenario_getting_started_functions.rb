# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose

# Shows how to use the AWS SDK for Python (Boto3) with AWS Lambda to do the following:

# 1. Create an AWS Identity and Access Management (IAM) role that grants Lambda
# permission to write to logs.
# 2. Create a Lambda function and upload handler code.
# 3. Invoke the function with a single parameter and get results.
# 4. Update the function code and configure its Lambda environment with an environment
# variable.
# 5. Invoke the function with new parameters and get results. Display the execution
# log that's returned from the invocation.
# 6. List the functions for your account.
# 7. Delete the IAM role and the Lambda function.

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
# @param iam_client: An SDK for Ruby v3 IAM client.
# @param cloudwatch_client: An SDK for Ruby v3 CloudWatch client.
# @param source_file: The name of the file that contains the basic Lambda handler.
# @param custom_name: The name to give resources created for the scenario, such as the
# IAM role and the Lambda function.
def run_scenario(lambda_client, iam_client, cloudwatch_client, source_file, custom_name)
  puts '-' * 88
  puts 'Welcome to the AWS Lambda getting started with functions demo.'
  puts '-' * 88

  wrapper = LambdaWrapper.new(lambda_client, iam_client, cloudwatch_client, source_file, custom_name)

  puts 'Creating IAM role for Lambda...'
  role_arn = wrapper.create_iam_role(custom_name)
  iam_client.wait_until(:role_exists, { role_name: custom_name }) do |w|
    w.max_attempts = 5
    w.delay = 5
  end


  if wrapper.get_function(custom_name).nil?
    deployment_package = wrapper.create_deployment_package(source_file, 'lambda_handler.zip')
    wrapper.create_function(custom_name, "#{source_file}.lambda_handler", role_arn, deployment_package)
    puts '-' * 88
  end

  puts "Let's invoke #{custom_name}."
  response = wrapper.invoke_function(custom_name)
  if response
    streams = cloudwatch_client.describe_log_streams({ log_group_name: "/aws/lambda/#{custom_name}" })
    puts "Great! Function invoked. Logs written to CloudWatch log stream: #{streams["log_streams"][0]["log_stream_name"]}."
    puts '-' * 88
  else
    puts "Invocation failed."
  end
  
  puts "Let's update our function code!"
  response = wrapper.update_function_code(custom_name, wrapper.create_deployment_package(source_file, 'lambda_handler.zip'))
  puts "Success!" if response
  
  puts "Let's update our function configurations!"
  response = lambda_client.update_function_configuration(
    function_name: custom_name,
    description: "Lambda function to test the SDK"
  )
  puts "Success!" if response

  puts "That's a wrap! Deleting IAM role and function..."
  wrapper.delete_function(custom_name)
  wrapper.delete_iam_role(custom_name)

end

lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')
iam_client = Aws::IAM::Client.new(region: 'us-east-1')
cloudwatch_client = Aws::CloudWatchLogs::Client.new(region: 'us-east-1')
source_file = 'lambda_function.rb'
custom_name = "lambda-party-#{rand(10**4)}"

run_scenario(lambda_client, iam_client, cloudwatch_client, source_file, custom_name) if __FILE__ == $PROGRAM_NAME

  #   puts "Let's update the function to an arithmetic calculator."
  #   q.ask("Press Enter when you're ready."
  #   puts "Creating a new deployment package..."
  #   deployment_package = wrapper.create_deployment_package(calculator_file, f"{lambda_name}.py"
  #   puts "...and updating the {lambda_name} Lambda function."
  #   update_waiter = UpdateFunctionWaiter(lambda_client)
  #   wrapper.update_function_code(lambda_name, deployment_package)
  #   update_waiter.wait(lambda_name)
  #   puts "This function uses an environment variable to control logging level."
  #   puts "Let's set it to DEBUG to get the most logging."
  #   wrapper.update_function_configuration(
  #     lambda_name, {'LOG_LEVEL': logging.getLevelName(logging.DEBUG)})
  #
  #   actions = ['plus', 'minus', 'times', 'divided-by']
  #   want_invoke = True
  #   while want_invoke:
  #     puts "Let's invoke {lambda_name}. You can invoke these actions:"
  #     for index, action in enumerate(actions):
  #       puts "{index + 1}: {action}"
  #       action_params = {}
  #       action_index = q.ask(
  #         "Enter the number of the action you want to take: ",
  #         q.is_int, q.in_range(1, len(actions)))
  #       action_params['action'] = actions[action_index - 1]
  #       puts "You've chosen to invoke 'x {action_params['action']} y'."
  #       action_params['x'] = q.ask("Enter a value for x: ", q.is_int)
  #       action_params['y'] = q.ask("Enter a value for y: ", q.is_int)
  #       puts "Invoking {lambda_name}..."
  #       response = wrapper.invoke_function(lambda_name, action_params, True)
  #       puts "Calculating {action_params['x']} {action_params['action']} {action_params['y']} "
  #       f"resulted in {json.load(response['Payload'])}"
  #       q.ask("Press Enter to see the logs from the call."
  #       puts base64.b64decode(response['LogResult']).decode())
  #       want_invoke = q.ask("That was fun. Shall we do it again? (y/n) ", q.is_yesno)
  #       puts '-'*88)
  #
  #       if q.ask("Do you want to list all of the functions in your account? (y/n) ":
  #                  wrapper.list_functions()
  #         puts '-'*88)
  #
  #         if q.ask("Ready to delete the function and role? (y/n) ", q.is_yesno):
  #           for policy in iam_role.attached_policies.all():
  #             policy.detach_role(RoleName=iam_role.name)
  #             iam_role.delete()
  #             puts "Deleted role {lambda_name}."
  #             wrapper.delete_function(lambda_name)
  #             puts "Deleted function {lambda_name}."
  #
  #             puts "\nThanks for watching!"
  #             puts '-'*88)

# lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')
#
# if File.exists?('lambda_function.zip')
#   system('rm lambda_function.zip')
#   puts "Deleted zip"
# end
#
# system('zip lambda_function.zip lambda_function.rb')
# puts "Created zip"
#
# def my_scenario(lambda_client)
#   iam_client = Aws::IAM::Client.new(region: 'us-east-1')
#
#   # PRE-REQUISITE RESOURCES
#   # Role with solitary AWSLambdaExecute policy, including S3 Get/Put Object & DenyAllExcept
#   iam_role = "arn:aws:iam::260778392212:role/lambda-role-#{@random_number}"
#   lambda_assume_role_policy = {
#     'Version': '2012-10-17',
#     'Statement': [
#       {
#         'Effect': 'Allow',
#         'Principal': {
#           'Service': 'lambda.amazonaws.com'
#         },
#         'Action': 'sts:AssumeRole'
#       }
#     ]
#   }
#   iam_client.create_role(
#     role_name: "lambda-role-#{@random_number}",
#     assume_role_policy_document: lambda_assume_role_policy.to_json
#   )
#   iam_client.attach_role_policy(
#     {
#       policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
#       role_name: "lambda-role-#{@random_number}"
#     }
#   )
#
#   # Allow IAM a moment to create
#   sleep(10)
#
#   function_name = "lambda-function-#{@random_number}"
#   args = { code: {} }
#   args[:role] = "arn:aws:iam::260778392212:role/lambda-role-#{@random_number}"
#   args[:function_name] = function_name
#   args[:handler] = 'lambda_function.lambda_handler'
#   args[:runtime] = 'ruby2.7'
#   args[:code][:zip_file] = File.read('lambda_function.zip')
#   resp = lambda_client.create_function(args)
#   function_arn = resp.function_arn
# end
#
# # # Add permission for bucket
# # bucket_name = "doc-example-bucket-#{@random_number}"
# # args = {}
# # args[:function_name] = function_name
# # args[:statement_id] = 'lambda_s3_notification'
# # args[:action] = 'lambda:InvokeFunction'
# # args[:principal] = 's3.amazonaws.com'
# # args[:source_arn] = "arn:aws:s3:::#{bucket_name}"
# # lambda_client.add_permission(args)
# #
# # s3_client = Aws::S3::Client.new(region: 'us-east-1')
# # s3_client.create_bucket(bucket: bucket_name)
# # s3_client.put_bucket_notification_configuration(
# #   {
# #     bucket: bucket_name,
# #     notification_configuration:
# #       {
# #         lambda_function_configurations: [
# #           {
# #             events: [
# #               "s3:ObjectCreated:*"
# #             ],
# #             lambda_function_arn: function_arn
# #           }
# #         ]
# #       }
# #   }
# # )
# #
# # # GetFunction
# my_scenario(lambda_client)
#
# sleep(10)
#
# functions = lambda_client.list_functions()
# name = functions["functions"][0]["function_name"].to_s
# lambda_client.invoke(
#   function_name: name,
#   invocation_type: "Event"
# )
# # # Invoke
# # # UpdateFunctionCode
# # # UpdateFunctionConfiguration
# # # DeleteFunction
