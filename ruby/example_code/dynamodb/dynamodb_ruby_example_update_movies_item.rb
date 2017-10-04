# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk-dynamodb'

# Create dynamodb client in us-west-2 region
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

params = {
    table_name: 'Movies',
    key: {
        year: 2015,
        title: 'The Big New Movie'
    },
    update_expression: 'set info.rating = :r',
    expression_attribute_values: {':r' => 0.1},
    return_values: 'UPDATED_NEW'
}

begin
  result = dynamodb.update_item(params)
  puts 'Rating successfully set'
rescue  Aws::DynamoDB::Errors::ServiceError => error
  puts 'Unable to set rating:'
  puts error.message
end
