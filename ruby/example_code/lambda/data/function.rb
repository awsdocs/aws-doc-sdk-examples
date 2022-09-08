require 'json'
require 'aws-sdk-s3'

def lambda_handler(event:, context:)
  puts "SOURCE: #{context.to_s}"
  bucket = event["Records"][0]["s3"]["bucket"]["name"]
  key = event["Records"][0]["s3"]["object"]["key"]
  s3 = Aws::S3::Object.new(bucket, key)
  type = s3.content_type
  puts "CONTENT TYPE: #{type}"
end