# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Creates a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_name [String] The name of the table to create.
# @param attribute_definitions [Hash] The properties of the fundamental data
#   elements of the table (such as a unique item identifier or a customer's
#   name), in the correct format.
# @param key_schema [Hash] The properties of the table's simple or composite
#   primary key, in the correct format.
# @param global_secondary_indexes [Hash] The properties of any alternative
#   keys for searching for items in the table, in the correct format.
# @param provisioned_throughput [Hash] Properties specifying the maximum
#   amount of capacity that an application can consume from the table or
#   one of its indexes, in the correct format.
# @return [Boolean] true if the table was created; otherwise, false.
# @example
#   exit 1 unless table_created?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     'Users',
#     [
#       {
#         attribute_name: 'ID',
#         attribute_type: 'N'
#       },
#       {
#         attribute_name: 'FirstName',
#         attribute_type: 'S'
#       },
#       {
#         attribute_name: 'LastName',
#         attribute_type: 'S'
#       }
#     ],
#     [
#       {
#         attribute_name: 'ID',
#         key_type: 'HASH'
#       }
#     ],
#     [
#       {
#         index_name: 'LastNameFirstNameIndex',
#         key_schema: [
#           {
#             attribute_name: 'FirstName',
#             key_type: 'HASH'
#           },
#           {
#             attribute_name: 'LastName',
#             key_type: 'RANGE'
#           }
#         ],
#         projection: { projection_type: 'ALL' },
#         provisioned_throughput: {
#           read_capacity_units: 5,
#           write_capacity_units: 10
#         }
#       }
#     ],
#     {
#       read_capacity_units: 5,
#       write_capacity_units: 10
#     }
#   )
def table_created?(
  dynamodb_client,
  table_name,
  attribute_definitions,
  key_schema,
  global_secondary_indexes,
  provisioned_throughput
)
  dynamodb_client.create_table(
    {
      table_name: table_name,
      attribute_definitions: attribute_definitions,
      key_schema: key_schema,
      global_secondary_indexes: global_secondary_indexes,
      provisioned_throughput: provisioned_throughput
    }  
  )
  dynamodb_client.wait_until(:table_exists, table_name: table_name)
  true
rescue StandardError => e
  puts "Error creating table: #{e.message}"
  false
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Users'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  attribute_definitions = [
    {
      attribute_name: 'ID',
      attribute_type: 'N'
    },
    {
      attribute_name: 'FirstName',
      attribute_type: 'S'
    },
    {
      attribute_name: 'LastName',
      attribute_type: 'S'
    }
  ]

  key_schema = [
    {
      attribute_name: 'ID',
      key_type: 'HASH'
    }
  ]

  global_secondary_indexes = [
    {
      index_name: 'LastNameFirstNameIndex',
      key_schema: [
        {
          attribute_name: 'FirstName',
          key_type: 'HASH'
        },
        {
          attribute_name: 'LastName',
          key_type: 'RANGE'
        }
      ],
      projection: { projection_type: 'ALL' },
      provisioned_throughput: {
        read_capacity_units: 5,
        write_capacity_units: 10
      }
    }
  ]

  provisioned_throughput = {
    read_capacity_units: 5,
    write_capacity_units: 10
  }

  puts "Creating table '#{table_name}' (this might take a minute)..."

  if table_created?(
    dynamodb_client,
    table_name,
    attribute_definitions,
    key_schema,
    global_secondary_indexes,
    provisioned_throughput
  )
    puts 'Table created.'
  else
    puts 'Table not created.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
