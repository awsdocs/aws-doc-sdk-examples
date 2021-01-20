# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# A global secondary index has a hash/partition key and a range/sort key that
# can be different from those on the base table.
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
  dynamodb_client.wait_until(:table_exists, table_name: 'Users')
  true
rescue StandardError => e
  puts "Error creating table: #{e.message}"
  false
end

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
