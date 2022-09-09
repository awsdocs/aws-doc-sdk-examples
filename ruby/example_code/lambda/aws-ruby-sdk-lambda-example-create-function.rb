# frozen_string_literal: true

require 'aws-sdk-lambda'
require 'aws-sdk-s3'
require 'aws-sdk-iam'
require 'logger'
require 'json'
require 'zip'


@logger = Logger.new($stdout)
@logger.level = Logger::WARN
@random_number = rand(10**4)

lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')

if File.exists?('lambda_function.zip')
  system('rm lambda_function.zip')
  puts "Deleted zip"
end

system('zip lambda_function.zip lambda_function.rb')
puts "Created zip"

def my_scenario(lambda_client)
  iam_client = Aws::IAM::Client.new(region: 'us-east-1')

  # PRE-REQUISITE RESOURCES
  # Role with solitary AWSLambdaExecute policy, including S3 Get/Put Object & DenyAllExcept
  iam_role = "arn:aws:iam::260778392212:role/lambda-role-#{@random_number}"
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
  role = iam_client.create_role(
    role_name: "lambda-role-#{@random_number}",
    assume_role_policy_document: lambda_assume_role_policy.to_json
  )
  iam_client.attach_role_policy(
    {
      policy_arn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole',
      role_name: "lambda-role-#{@random_number}"
    }
  )

  # Allow IAM a moment to create
  sleep(10)

  function_name = "lambda-function-#{@random_number}"
  args = { code: {} }
  args[:role] = "arn:aws:iam::260778392212:role/lambda-role-#{@random_number}"
  args[:function_name] = function_name
  args[:handler] = 'lambda_function.lambda_handler'
  args[:runtime] = 'ruby2.7'
  args[:code][:zip_file] = File.read('lambda_function.zip')
  resp = lambda_client.create_function(args)
  function_arn = resp.function_arn
end

# # Add permission for bucket
# bucket_name = "doc-example-bucket-#{@random_number}"
# args = {}
# args[:function_name] = function_name
# args[:statement_id] = 'lambda_s3_notification'
# args[:action] = 'lambda:InvokeFunction'
# args[:principal] = 's3.amazonaws.com'
# args[:source_arn] = "arn:aws:s3:::#{bucket_name}"
# lambda_client.add_permission(args)
#
# s3_client = Aws::S3::Client.new(region: 'us-east-1')
# s3_client.create_bucket(bucket: bucket_name)
# s3_client.put_bucket_notification_configuration(
#   {
#     bucket: bucket_name,
#     notification_configuration:
#       {
#         lambda_function_configurations: [
#           {
#             events: [
#               "s3:ObjectCreated:*"
#             ],
#             lambda_function_arn: function_arn
#           }
#         ]
#       }
#   }
# )
#
# # GetFunction
my_scenario(lambda_client)

sleep(10)

functions = lambda_client.list_functions()
name = functions["functions"][0]["function_name"].to_s
lambda_client.invoke(
  function_name: name,
  invocation_type: "Event"
)
# # Invoke
# # UpdateFunctionCode
# # UpdateFunctionConfiguration
# # DeleteFunction
