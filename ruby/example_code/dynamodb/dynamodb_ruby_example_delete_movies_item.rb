# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Removes an item from a DynamoDB table.]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[delete_item method]
# snippet-keyword:[Ruby]
# snippet-service:[dynamodb]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-dynamodb'  # v2: require 'aws-sdk'

# Create dynamodb client in us-west-2 region
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

params = {
    table_name: 'Movies',
    key: {
        year: 2015,
        title: 'The Big New Movie'
    }
}

begin
  dynamodb.delete_item(params)
  puts 'Deleted movie'
rescue  Aws::DynamoDB::Errors::ServiceError => error
  puts 'Unable to delete movie:'
  puts error.message
end
