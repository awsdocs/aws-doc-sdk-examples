# snippet-sourcedescription:[MoviesCreateTable.rb demonstrates how to ]
# snippet-service:[dynamodb]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Code Sample]
# snippet-keyword:[ ]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[ ]
# snippet-sourceauthor:[AWS]
# snippet-start:[dynamodb.Ruby.CodeExample.MoviesCreateTable] 

#
#  Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
#  This file is licensed under the Apache License, Version 2.0 (the "License").
#  You may not use this file except in compliance with the License. A copy of
#  the License is located at
# 
#  http://aws.amazon.com/apache2.0/
# 
#  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#  CONDITIONS OF ANY KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations under the License.
#
require "aws-sdk"

Aws.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000"
})

dynamodb = Aws::DynamoDB::Client.new

params = {
    table_name: "Movies",
    key_schema: [
        {
            attribute_name: "year",
            key_type: "HASH"  #Partition key
        },
        {
            attribute_name: "title",
            key_type: "RANGE" #Sort key 
        }
    ],
    attribute_definitions: [
        {
            attribute_name: "year",
            attribute_type: "N"
        },
        {
            attribute_name: "title",
            attribute_type: "S"
        },

    ],
    provisioned_throughput: { 
        read_capacity_units: 10,
        write_capacity_units: 10
  }
}

begin
    result = dynamodb.create_table(params)
    puts "Created table. Status: " + 
        result.table_description.table_status;

rescue  Aws::DynamoDB::Errors::ServiceError => error
    puts "Unable to create table:"
    puts "#{error.message}"
end
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesCreateTable]