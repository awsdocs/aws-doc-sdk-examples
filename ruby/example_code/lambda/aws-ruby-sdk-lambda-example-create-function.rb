require 'aws-sdk-lambda'
require 'aws-sdk-s3'
require 'logger'

@logger = Logger.new(STDOUT)
@logger.level = Logger::WARN
@random_number = rand(10 ** 4)

lambda_client = Aws::Lambda::Client.new(region: 'us-east-1')

# PRE-REQUISITE RESOURCES
# Role with solitary AWSLambdaExecute policy, including S3 Get/Put Object
iam_role = 'arn:aws:iam::260778392212:role/lambda-role'

function_name = "lambda-function-#{@random_number}"
args = { code: {} }
args[:role] = iam_role
args[:function_name] = function_name
args[:handler] = 'function.lambda_handler'
args[:runtime] = 'ruby2.7'
args[:code][:zip_file] = File.read('data/function.zip')
resp = lambda_client.create_function(args)
function_arn = resp.function_arn

# Add permission for bucket
bucket_name = "doc-example-bucket-#{@random_number}"
args = {}
args[:function_name] = function_name
args[:statement_id] = 'lambda_s3_notification'
args[:action] = 'lambda:InvokeFunction'
args[:principal] = 's3.amazonaws.com'
args[:source_arn] = "arn:aws:s3:::#{bucket_name}"
lambda_client.add_permission(args)

s3_client = Aws::S3::Client.new(region: 'us-east-1')
s3_client.create_bucket(bucket: bucket_name)
s3_client.put_bucket_notification_configuration(
  {
    bucket: bucket_name,
    notification_configuration:
      {
        lambda_function_configurations: [
          {
            events: [
              "s3:ObjectCreated:*"
            ],
            lambda_function_arn: function_arn
          }
        ]
      }
  }
)

# yay!
