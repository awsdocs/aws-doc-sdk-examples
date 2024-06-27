# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.dynamodb.hello_dynamodb]

require 'aws-sdk-dynamodb'

# Create a DynamoDB client using the default credentials and region
dynamodb = Aws::DynamoDB::Client.new

# List the tables in the current AWS account
puts "Here are the DynamoDB tables in your account:"

# Use pagination to list all tables, limiting the number of results per page
table_names = []
last_table_name = nil
loop do
  list_tables_response = dynamodb.list_tables(
    exclusive_start_table_name: last_table_name,
    limit: 10
  )

  list_tables_response.table_names.each do |table_name|
    puts "- #{table_name}"
    table_names << table_name
  end

  if list_tables_response.last_evaluated_table_name
    last_table_name = list_tables_response.last_evaluated_table_name
  else
    break
  end
end

if table_names.empty?
  puts "You don't have any DynamoDB tables in your account."
else
  puts "\nFound #{table_names.length} tables."
end

# snippet-end:[ruby.dynamodb.hello_dynamodb]
