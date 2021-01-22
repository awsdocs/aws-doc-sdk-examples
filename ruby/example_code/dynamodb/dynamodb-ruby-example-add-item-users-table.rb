# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

def item_added_to_table?(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  true
rescue StandardError => e
  puts "Error adding item: #{e.message}"
  false
end

def run_me
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
