# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Gets an item from a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_item [Hash] The properties of the item to get,
#   in the correct format.
# @example
#   get_item_from_table(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Users',
#       key: { 'ID': 123456 }
#     }
#   )
def get_item_from_table(dynamodb_client, table_item)
  result = dynamodb_client.get_item(table_item)
  if result.item.nil?
    puts 'No matching item found.'
  else
    puts "User ID:    #{result.item['ID'].to_i}"
    puts "First name: #{result.item['FirstName']}"
    puts "Last name:  #{result.item['LastName']}"
  end
rescue StandardError => e
  puts "Error getting item from table: #{e.message}"
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Users'
  user_id = 123456

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  table_item = {
    table_name: table_name,
    key: { 'ID': user_id }
  }

  puts "Getting information about user with ID '#{user_id}' " \
    "from table '#{table_name}'..."
  get_item_from_table(dynamodb_client, table_item)
end

run_me if $PROGRAM_NAME == __FILE__