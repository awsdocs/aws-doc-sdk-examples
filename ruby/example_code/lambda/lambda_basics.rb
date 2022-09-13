# frozen_string_literal: true

# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS Lambda
# functions.

require 'aws-sdk-lambda'
require 'aws-sdk-s3'
require 'aws-sdk-iam'
require 'logger'
require 'json'
require 'zip'

@logger = Logger.new($stdout)

# snippet-start:[ruby.example_code.ruby.LambdaWrapper.full]
# snippet-start:[ruby.example_code.ruby.LambdaWrapper.decl]
class LambdaWrapper
  attr_accessor :iam_client, :lambda_client, :cloudwatch_client, :source_file, :custom_name

  def initialize(lambda_client, iam_client, cloudwatch_client, source_file, custom_name)
    @lambda_client = lambda_client
    @iam_client = iam_client
    @cloudwatch_client = cloudwatch_client
    @source_file = source_file
    @custom_name = custom_name
  end

  # Creates a Lambda deployment package in .zip format in an in-memory buffer. This
  # buffer can be passed directly to Lambda when creating the function.
  #
  # @param source_file: The name of the file that contains the Lambda handler
  # function.
  # @param destination_file: The name to give the file when it's deployed to Lambda.
  # @return: The deployment package.
  def create_deployment_package(source_file = 'lambda_function.rb', destination_file = 'lambda_function.zip')
    if File.exist?(destination_file)
      system("rm #{destination_file}")
    end
    system("zip #{destination_file} #{source_file}")
    File.read(destination_file.to_s)
  end

  # Get an AWS Identity and Access Management (IAM) role.
  #
  # @param iam_role_name: The name of the role to retrieve.
  # @return: The IAM role.
  def create_iam_role(iam_role_name)
    # Role with solitary AWSLambdaExecute policy, including S3 Get/Put Object & DenyAllExcept
    lambda_assume_role_policy = {
      'Version': '2012-10-17',
      'Statement': [
        {
          'Effect': 'Allow',
          'Principal': {
            'Service': 'lambda.amazonaws.com'
          },
          'Action': 'sts:AssumeRole'
        }
      ]
    }
    role = @iam_client.create_role(
      role_name: iam_role_name,
      assume_role_policy_document: lambda_assume_role_policy.to_json
    )
    @iam_client.attach_role_policy(
      {
        policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
        role_name: iam_role_name
      }
    )
    puts "Successfully created IAM role: #{role["role"]["arn"]}"
    sleep(10)
    role["role"]["arn"]
  end

  def delete_iam_role(iam_role_name)
    @iam_client.detach_role_policy(
      {
        policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
        role_name: iam_role_name
      }
    )
    @iam_client.delete_role(
      role_name: iam_role_name
    )
    puts "Detached policy & deleted IAM role: #{iam_role_name}"
  end

  # snippet-start:[ruby.example_code.lambda.GetFunction]
  # Gets data about a Lambda function.
  #
  # @param function_name: The name of the function.
  # @return response: The function data.
  def get_function(function_name)
    @lambda_client.get_function(
      {
        function_name: function_name
      }
    )
  rescue Aws::Lambda::Errors::ResourceNotFoundException
    nil
  end
  # snippet-end:[ruby.example_code.lambda.GetFunction]

  # snippet-start:[ruby.example_code.lambda.CreateFunction]
  # Deploys a Lambda function.
  #
  # @param function_name: The name of the Lambda function.
  # @param handler_name: The fully qualified name of the handler function. This
  #                      must include the file name and the function name.
  # @param role_arn: The IAM role to use for the function.
  # @param deployment_package: The deployment package that contains the function
  #                            code in .zip format.
  # @return: The Amazon Resource Name (ARN) of the newly created function.
  def create_function(function_name, handler_name, role_arn, deployment_package)
    # args = { code: {} }
    # args[:role] = role_arn # "arn:aws:iam::260778392212:role/lambda-role-#{@random_number}"
    # args[:function_name] = function_name
    # args[:handler] = handler_name # 'lambda_function.lambda_handler'
    # args[:runtime] = 'ruby2.7'
    # args[:code][:zip_file] = deployment_package # File.read('lambda_function.zip')
    @lambda_client.create_function(
      response = {
        role: role_arn.to_s,
        function_name: function_name,
        handler: handler_name,
        runtime: 'ruby2.7',
        code: {
          zip_file: deployment_package
        }
      }
    )
    @lambda_client.wait_until(:function_active_v2, { function_name: function_name }) do |w|
      w.max_attempts = 5
      w.delay = 5
    end
    response["function_arn"]
  end
  # snippet-end:[ruby.example_code.lambda.CreateFunction]

  # snippet-start:[ruby.example_code.lambda.DeleteFunction]
  # Deletes a Lambda function.
  # @param function_name: The name of the function to delete
  def delete_function(function_name)
    @lambda_client.delete_function(
      function_name: function_name
    )
    puts "Deleted function: #{function_name}"
  end
  # snippet-end:[ruby.example_code.lambda.DeleteFunction]

  # snippet-start:[ruby.example_code.lambda.Invoke]
  # Invokes a Lambda function.
  # @param function_name: The name of the function to invoke.
  # @return: The response from the function invocation.
  def invoke_function(function_name)
    @lambda_client.invoke(
      function_name: function_name
    )
  rescue Aws::Lambda::Errors::ServiceException
    nil
  end

  # snippet-end:[ruby.example_code.lambda.Invoke]

  # snippet-start:[ruby.example_code.lambda.UpdateFunctionCode]
  # Updates the code for a Lambda function by submitting a .zip archive that contains
  # the code for the function.

  # @param function_name: The name of the function to update.
  # @param deployment_package: The function code to update, packaged as bytes in
  #                            .zip format.
  # @return: Data about the update, including the status.
  def update_function_code(function_name, deployment_package)
    @lambda_client.update_function_code(
      function_name: function_name,
      zip_file: deployment_package
    )
    @lambda_client.wait_until(:function_updated_v2, { function_name: function_name }) do |w|
      w.max_attempts = 5
      w.delay = 5
    rescue Aws::Lambda::Errors::ServiceException
      nil
    end
    # try:
    #     response = self.lambda_client.update_function_code(
    #         FunctionName=function_name, ZipFile=deployment_package)
    # except ClientError as err:
    #     logger.error(
    #         "Couldn't update function %s. Here's why: %s: %s", function_name,
    #         err.response['Error']['Code'], err.response['Error']['Message'])
    #     raise
    # else:
    #     return response
  end
  # snippet-end:[ruby.example_code.lambda.UpdateFunctionCode]

  # snippet-start:[ruby.example_code.lambda.UpdateFunctionConfiguration]
  # Updates the environment variables for a Lambda function.

  # @param function_name: The name of the function to update.
  # @param env_vars: A dict of environment variables to update.
  # @return: Data about the update, including the status.
  def update_function_configuration(function_name, env_vars)
    @lambda_client.update_function_configuration(
      function_name: function_name,
      env_vars: env_vars
    )
    @lambda_client.wait_until(:function_updated_v2, { function_name: function_name }) do |w|
      w.max_attempts = 5
      w.delay = 5
    end
  end
  # snippet-end:[ruby.example_code.lambda.UpdateFunctionConfiguration]

  # snippet-start:[ruby.example_code.lambda.ListFunctions]
  # Lists the Lambda functions for the current account.
  def list_functions
    @lambda_client.list_functions.each do |response|
      puts response.contents.map(&:key)
      # name = functions["functions"][0]["function_name"].to_s
    end
  end
  # snippet-end:[ruby.example_code.lambda.ListFunctions]
  # snippet-end:[ruby.example_code.python.LambdaWrapper.full]
end
