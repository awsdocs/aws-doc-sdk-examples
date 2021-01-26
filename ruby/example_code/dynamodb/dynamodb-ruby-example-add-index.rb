# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Adds an index to a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param index_definition [Hash] The properties of the index, in the correct format.
# @return [Boolean] true if the index was added; otherwise, false.
# @example
#   exit 1 unless index_added?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     index_definition = {
#       table_name: 'Users',
#       attribute_definitions: [
#         {
#           attribute_name: 'AirMiles',
#           attribute_type: 'N'
#         }
#       ],
#       global_secondary_index_updates: [
#         {
#           create: {
#             index_name: 'AirMileageIndex',
#             key_schema: [
#               {
#                 attribute_name: 'AirMiles',
#                 key_type: 'HASH'
#               }
#             ],
#             projection: {
#               projection_type: 'ALL'
#             },
#             provisioned_throughput: {
#               read_capacity_units: 5,
#               write_capacity_units: 10
#             }
#           }
#         }
#       ]
#     }
#   )
def index_added?(dynamodb_client, index_definition)
  dynamodb_client.update_table(index_definition)
  true
rescue StandardError => e
  puts "Error creating index: #{e.message}"
  false
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Users'
  index_name = 'AirMileageIndex'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  index_definition = {
    table_name: table_name,
    attribute_definitions: [
      {
        attribute_name: 'AirMiles',
        attribute_type: 'N'
      }
    ],    
    global_secondary_index_updates: [
      {
        create: {
          index_name: index_name,
          key_schema: [
            {
              attribute_name: 'AirMiles',
              key_type: 'HASH'
            }
          ],
          projection: {
            projection_type: 'ALL'
          },
          provisioned_throughput: {
            read_capacity_units: 5,
            write_capacity_units: 10
          }
        }
      }
    ]
  }

  puts "Adding index '#{index_name}' to table '#{table_name}'..."

  if index_added?(dynamodb_client, index_definition)
    puts 'Index created.'
  else
    puts 'Index not created.'
  end

end

run_me if $PROGRAM_NAME == __FILE__
