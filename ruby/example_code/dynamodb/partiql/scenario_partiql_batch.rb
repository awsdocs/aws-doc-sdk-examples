# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# To run this demo, you will need Ruby 2.6 or later, plus dependencies.
# For more information, see:
# https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md

require "aws-sdk-dynamodb"
require "logger"
require "json"
require "zip"
require "cli/ui"
require_relative("../../../helpers/disclaimers")
require_relative("../../../helpers/decorators")
require_relative("partiql_batch")

# Runs the Amazon DynamoDB PartiQL demo.
# @return [Nil]
def run_scenario
  banner
  puts "######################################################################################################".yellow
  puts "#                                                                                                    #".yellow
  puts "#                                          EXAMPLE CODE DEMO:                                        #".yellow
  puts "#                                       Amazon DynamoDB - PartiQL                                    #".yellow
  puts "#                                                                                                    #".yellow
  puts "######################################################################################################".yellow
  puts ""
  puts "You have launched a demo of Amazon DynamoDB using PartiQL and the AWS for Ruby v3 SDK. Over the next 60 seconds, it will"
  puts "do the following:"
  puts "    1. Create a new DynamoDB table."
  puts "    2. Write batch data into the table"
  puts "    3. Get a batch of items from a table using PartiQL."
  puts "    4. Delete a batch of from a table using PartiQL."
  puts "    5. Destroy the DynamoDB table."
  puts ""

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  # snippet-start:[ruby.example_code.dynamodb.Scenario_PartiQL_Batch]
  table_name = "doc-example-table-movies-partiql-#{rand(10**4)}"
  scaffold = Scaffold.new(table_name)
  sdk = DynamoDBPartiQLBatch.new(table_name)

  new_step(1, "Create a new DynamoDB table if none already exists.")
  unless scaffold.exists?(table_name)
    puts("\nNo such table: #{table_name}. Creating it...")
    scaffold.create_table(table_name)
    print "Done!\n".green
  end

  new_step(2, "Populate DynamoDB table with movie data.")
  download_file = "moviedata.json"
  puts("Downloading movie database to #{download_file}...")
  movie_data = scaffold.fetch_movie_data(download_file)
  puts("Writing movie data from #{download_file} into your table...")
  scaffold.write_batch(movie_data)
  puts("Records added: #{movie_data.length}.")
  print "Done!\n".green

  new_step(3, "Select a batch of items from the movies table.")
  puts "Let's select some popular movies for side-by-side comparison."
  response = sdk.batch_execute_select([["Mean Girls", 2004], ["Goodfellas", 1977], ["The Prancing of the Lambs", 2005]])
  puts("Items selected: #{response['responses'].length}\n")
  print "\nDone!\n".green

  new_step(4, "Delete a batch of items from the movies table.")
  sdk.batch_execute_write([["Mean Girls", 2004], ["Goodfellas", 1977], ["The Prancing of the Lambs", 2005]])
  print "\nDone!\n".green

  new_step(5, "Delete the table.")
  if scaffold.exists?(table_name)
    scaffold.delete_table
  end
end
# snippet-end:[ruby.example_code.dynamodb.Scenario_PartiQL_Batch]

run_scenario if __FILE__ == $PROGRAM_NAME
