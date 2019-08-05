# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[dynamodb_ruby_example_delete_movies_table.rb deletes the DynamoDb table Movies.]
# snippet-service:[dynamodb]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Code Sample]
# snippet-keyword:[delete_table]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-3-27]
# snippet-sourceauthor:[Doug-AWS]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
# snippet-start:[dynamodb.ruby.delete_table]
require 'aws-sdk-dynamodb' # v2: require 'aws-sdk'

# Create dynamodb client in us-west-2 region
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

params = {
  table_name: 'Movies'
}

begin
  dynamodb.delete_table(params)
  puts 'Deleted table.'
rescue Aws::DynamoDB::Errors::ServiceError => error
  puts 'Unable to delete table:'
  puts error.message
end
# snippet-end:[dynamodb.ruby.delete_table]
