#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Updates a DynamoDB table to include an index.]
#snippet-keyword:[Amazon DynamoDB]
#snippet-keyword:[update_table method]
#snippet-keyword:[Ruby]
#snippet-service:[dynamodb]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

request = {
  attribute_definitions: [
    {
      attribute_name: 'airmiles',
      attribute_type: 'N',
    },
  ],
  table_name: 'Users',
  global_secondary_index_updates: [
    {
      create: {
        index_name: 'air-mileage-index',
        key_schema: [
          {
            attribute_name: 'airmiles',
            key_type: 'HASH',
          },
        ],
        projection: {
          projection_type: 'ALL',
        },
        provisioned_throughput: {
          read_capacity_units: 5,
          write_capacity_units: 10,
        },
      },
    },
  ],
}

dynamoDB = Aws::DynamoDB::Client.new(region: 'us-west-2')

dynamoDB.update_table(request)
