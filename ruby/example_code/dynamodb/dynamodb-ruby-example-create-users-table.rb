#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates a DynamoDB table.]
#snippet-keyword:[Amazon DynamoDB]
#snippet-keyword:[create_table method]
#snippet-keyword:[wait_until method]
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

attribute_defs = [
  { attribute_name: 'ID',        attribute_type: 'N' },
  { attribute_name: 'FirstName', attribute_type: 'S' },
  { attribute_name: 'LastName',  attribute_type: 'S' }
]

key_schema = [
  { attribute_name: 'ID', key_type: 'HASH' }
]

index_schema = [
  { attribute_name: 'FirstName', key_type: 'HASH'  },
  { attribute_name: 'LastName',  key_type: 'RANGE' }
]

global_indexes = [{
  index_name:             'LastNameFirstNameIndex',
  key_schema:             index_schema,
  projection:             { projection_type: 'ALL' },
  provisioned_throughput: { read_capacity_units: 5, write_capacity_units: 10 }
}]

request = {
  attribute_definitions:    attribute_defs,
  table_name:               'Users',
  key_schema:               key_schema,
  global_secondary_indexes: global_indexes,
  provisioned_throughput:   { read_capacity_units: 5, write_capacity_units: 10 }
}

dynamodb_client = Aws::DynamoDB::Client.new(region: 'us-west-2')

dynamodb_client.create_table(request)
dynamodb_client.wait_until(:table_exists, table_name: 'Users')
