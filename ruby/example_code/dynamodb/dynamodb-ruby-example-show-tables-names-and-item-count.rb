# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

def get_table_names(dynamodb_client)
  result = dynamodb_client.list_tables
  result.table_names
rescue StandardError => e
  puts "Error getting table names: #{e.message}"
  'Error'
end

def get_count_of_table_items(dynamodb_client, table_name)
  result = dynamodb_client.scan(table_name: table_name)
  result.items.count
rescue StandardError => e
  puts "Error getting items for table '#{table_name}': #{e.message}"
  'Error'
end

def run_me
  region = 'us-west-2'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)
  table_names = get_table_names(dynamodb_client)

  if table_names == 'Error'
    puts 'Cannot get table names. Stopping program.'
  elsif table_names.count.zero?
    puts "Cannot find any tables in AWS Region '#{region}'."
  else
    puts "Found #{table_names.count} tables in AWS Region '#{region}':"
    puts "(Displaying information for only the first 100 tables)" if table_names.count > 100
    
    table_names.each do |table_name|
      table_items_count = get_count_of_table_items(dynamodb_client, table_name)

      if table_items_count == 'Error'
        puts "Cannot get count of items for table '#{table_name}'."
      elsif table_items_count.zero?
        puts "Table '#{table_name}' has no items."
      else
        puts "Table '#{table_name}' has #{table_items_count} items."
      end

    end
  end
end

run_me if $PROGRAM_NAME == __FILE__
