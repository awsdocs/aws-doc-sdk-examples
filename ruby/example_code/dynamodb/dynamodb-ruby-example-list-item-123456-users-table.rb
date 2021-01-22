# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

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