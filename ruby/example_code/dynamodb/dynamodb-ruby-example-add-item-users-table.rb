# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# dynamodb-ruby-example-add-item-users-table.rb demonstrates how to add an item to a table
# in Amazon DynamoDB using the AWS SDK for Ruby.

# snippet-start:[dynamodb.Ruby.addItemUserTable]
require 'aws-sdk-dynamodb'

#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_item [Hash] The properties of the item, in the correct format.
# @return [Boolean] true if the item was added; otherwise, false.
# @example
#   exit 1 unless item_added_to_table?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Users',
#       item: {
#         'ID': 123456,
#         'FirstName': 'John',
#         'LastName': 'Doe',
#         'AirMiles': 0
#       }
#     }
#   )
def item_added_to_table?(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  true
rescue StandardError => e
  puts "Error adding item: #{e.message}"
  false
end

# Full example call:
def run_me
# Replace us-west-2 with the AWS Region you're using for AWS DynamoDB.
  region = 'us-west-2'
  table_name = 'Users'
  user_id = 123456
  first_name = 'John'
  last_name = 'Doe'
  air_miles = 0

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  item = {
    'ID': user_id,
    'FirstName': first_name,
    'LastName': last_name,
    'AirMiles': air_miles
  }

  table_item = {
    table_name: table_name,
    item: item
  }

  puts "Adding user '#{item[:FirstName]} #{item[:LastName]}' " \
    "to table '#{table_name}'..."

  if item_added_to_table?(dynamodb_client, table_item)
    puts 'Item added.'
  else
    puts 'Item not added.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.addItemUserTable]
