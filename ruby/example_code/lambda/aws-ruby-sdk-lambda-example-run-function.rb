# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-run-function.rb demonstrates how to
# run an AWS Lambda function using the AWS SDK for Ruby.

# snippet-start:[lambda.ruby.runFunction]

require "aws-sdk-lambda"  # v2: require 'aws-sdk'
require "json"

# To run on Windows:
require "os"
if OS.windows?
  Aws.use_bundled_cert!
end
# Replace us-west-2 with the AWS Region you're using for Lambda.
client = Aws::Lambda::Client.new(region: "us-west-2")

# Get the 10 most recent items
req_payload = {SortBy: "time", SortOrder: "descending", NumberToGet: 10}
payload = JSON.generate(req_payload)

resp = client.invoke({
                         function_name: "MyGetItemsFunction",
                         invocation_type: "RequestResponse",
                         log_type: "None",
                         payload: payload
                       })

resp_payload = JSON.parse(resp.payload.string) # , symbolize_names: true)

# If the status code is 200, the call succeeded
if resp_payload["statusCode"] == 200
  # If the result is success, we got our items
  if resp_payload["body"]["result"] == "success"
    # Print out items
    resp_payload["body"]["data"].each do |item|
      puts item
    end
  end
end
# snippet-end:[lambda.ruby.runFunction]
