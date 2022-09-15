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

# snippet-start:[ruby.example_code.ruby.LambdaWrapper.full]
# snippet-start:[ruby.example_code.ruby.LambdaWrapper.decl]
class LambdaWrapper
  attr_accessor :lambda_client, :source_file, :custom_name, :cloudwatch_client, :iam_client

  def initialize(lambda_client, source_file, custom_name)
    @lambda_client = lambda_client
    @source_file = source_file
    @custom_name = custom_name
    @logger = Logger.new($stdout)
    @logger.level = Logger::INFO
    @cloudwatch_client = Aws::CloudWatchLogs::Client.new
    @iam_client = Aws::IAM::Client.new
  end

  # Creates a Lambda deployment package in .zip format
  # This zip can be passed directly as a string to Lambda when creating the function.
  #
  # @param source_file: The name of the object without suffix for Lambda file and zip.
  # @return: The deployment package.
  def create_deployment_package(source_file)
    Dir.chdir(File.dirname(__FILE__))
    if File.exist?("#{source_file}.zip")
      File.delete("#{source_file}.zip")
      @logger.debug("Deleting old zip: #{source_file}.zip")

    end
    Zip::File.open("#{source_file}.zip", create: true) {
      |zipfile|
      zipfile.add("#{source_file}.rb", "#{source_file}.rb")
    }

    # system("zip #{source_file}.zip #{source_file}.rb > /dev/null")
    @logger.debug("Zipping #{source_file}.rb into: #{source_file}.zip.")
    File.read("#{source_file}.zip").to_s
  end

  # Get an AWS Identity and Access Management (IAM) role.
  #
  # @param iam_role_name: The name of the role to retrieve.
  # @param action: whether to create or destroy the IAM apparatus
  # @return: The IAM role.
  def manage_iam(iam_role_name, action)
    role_policy = {
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
    case action
    when 'create'
      role = iam_client.create_role(
        role_name: iam_role_name,
        assume_role_policy_document: role_policy.to_json
      )
      @iam_client.attach_role_policy(
        {
          policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
          role_name: iam_role_name
        }
      )
      @iam_client.wait_until(:role_exists, { role_name: iam_role_name }) do |w|
        w.max_attempts = 5
        w.delay = 5
      end
      @logger.debug("Successfully created IAM role: #{role['role']['arn']}")
      @logger.debug('Enforcing a 10-second sleep to allow IAM role to activate fully.')
      sleep(10)
      return role, role_policy.to_json
    when 'destroy'
      @iam_client.detach_role_policy(
        {
          policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
          role_name: iam_role_name
        }
      )
      @iam_client.delete_role(
        role_name: iam_role_name
      )
      @logger.debug("Detached policy & deleted IAM role: #{iam_role_name}")
    else
      raise "Incorrect action provided. Must provide 'create' or 'destroy'"
    end
  end

  # Get function logs from the latest CloudWatch log stream
  # @param function_name: The name of the function
  # @param string_match: A string to look for in the logs
  # @return all_logs: an array of all the log messages found for that stream
  def get_cloudwatch_logs(function_name, string_match)
    @logger.debug("Enforcing a 10 second sleep to allow CloudWatch logs to appear.")
    sleep(10)
    streams = @cloudwatch_client.describe_log_streams({ log_group_name: "/aws/lambda/#{function_name}" })
    streams['log_streams'].each do |x|
      resp = @cloudwatch_client.get_log_events({
                                                log_group_name: "/aws/lambda/#{function_name}",
                                                log_stream_name: x['log_stream_name']
                                              })
      resp.events.each do |x|
        if "/#{x.message}/".match(string_match)
          msg = x.message.split(' -- : ')[1].green
          puts "CloudWatch log stream: #{msg}"
        end
      end
    end
  end

  # snippet-start:[ruby.example_code.lambda.GetFunction]
  # Gets data about a Lambda function.
  #
  # @param function_name: The name of the function.
  # @return response: The function data, or nil if no such function exists
  def get_function(function_name)
    @lambda_client.get_function(
      {
        function_name: function_name
      }
    )
  rescue Aws::Lambda::Errors::ResourceNotFoundException => e
    @logger.debug("Could not find function: #{function_name}:\n #{e.message}")
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
    response = @lambda_client.create_function({
                                                role: role_arn.to_s,
                                                function_name: function_name,
                                                handler: handler_name,
                                                runtime: 'ruby2.7',
                                                code: {
                                                  zip_file: deployment_package
                                                },
                                                environment: {
                                                  variables: {
                                                    'LOG_LEVEL' => 'info'
                                                  }
                                                }
                                              })
    @lambda_client.wait_until(:function_active_v2, { function_name: function_name }) do |w|
      w.max_attempts = 5
      w.delay = 5
    end
    print "Done!\n".green
    puts JSON.pretty_generate(response).green
    response['function_arn']
  rescue Aws::Lambda::Errors::ServiceException => e
    puts "There was an error creating #{function_name}:\n #{e.message}"
  rescue Aws::Waiters::Errors::WaiterFailed => e
    puts "Failed waiting for #{function_name} to activate:\n #{e.message}"
  end
  # snippet-end:[ruby.example_code.lambda.CreateFunction]

  # snippet-start:[ruby.example_code.lambda.DeleteFunction]
  # Deletes a Lambda function.
  # @param function_name: The name of the function to delete
  def delete_function(function_name)
    print "Deleted function: #{function_name}..."
    @lambda_client.delete_function(
      function_name: function_name
    )
    print 'Done!'
  rescue Aws::Lambda::Errors::ServiceException => e
    puts "There was an error deleting #{function_name}:\n #{e.message}"
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
  end
  # snippet-end:[ruby.example_code.lambda.UpdateFunctionCode]

  # snippet-start:[ruby.example_code.lambda.UpdateFunctionConfiguration]
  # Updates the environment variables for a Lambda function.
  # @param function_name: The name of the function to update.
  # @param log_level: The log level of the function
  # @return: Data about the update, including the status.
  def update_function_configuration(function_name, log_level)
    @lambda_client.update_function_configuration({
                                                   function_name: function_name,
                                                   environment: {
                                                     variables: {
                                                       'LOG_LEVEL' => log_level
                                                     }
                                                   }
                                                 })
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
