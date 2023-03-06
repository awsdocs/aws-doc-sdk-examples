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
require_relative("partiql_single")

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
  puts "You have launched a demo of Amazon DynamoDB using PartiQL and the AWS for Ruby v3 SDK. Over the next 60 seconds,it will"
  puts "do the following:"
  puts "    1. Create a new DynamoDB table."
  puts "    2. Write batch data into the table"
  puts "    3. Get a single item from a table using PartiQL"
  puts "    4. Update a single item in a table using PartiQL"
  puts "    5. Delete a single item from a table using PartiQL"
  puts "    6. Insert a single item into a table using PartiQL"
  puts "    7. Destroy the DynamoDB table."
  puts ""

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  # snippet-start:[ruby.example_code.dynamodb.Scenario_PartiQL_Single]
  table_name = "doc-example-table-movies-partiql-#{rand(10**8)}"
  scaffold = Scaffold.new(table_name)
  sdk = DynamoDBPartiQLSingle.new(table_name)

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

  new_step(3, "Select a single item from the movies table.")
  response = sdk.select_item_by_title("Star Wars")
  puts("Items selected for title 'Star Wars': #{response.items.length}\n")
  print "#{response.items.first}".yellow
  print "\n\nDone!\n".green

  new_step(4, "Update a single item from the movies table.")
  puts "Let's correct the rating on The Big Lebowski to 10.0."
  sdk.update_rating_by_title("The Big Lebowski", 1998, 10.0)
  print "\nDone!\n".green

  new_step(5, "Delete a single item from the movies table.")
  puts "Let's delete The Silence of the Lambs because it's just too scary."
  sdk.delete_item_by_title("The Silence of the Lambs", 1991)
  print "\nDone!\n".green

  new_step(6, "Insert a new item into the movies table.")
  puts "Let's create a less-scary movie called The Prancing of the Lambs."
  sdk.insert_item("The Prancing of the Lambs", 2005, "A movie about happy livestock.", 5.0)
  print "\nDone!\n".green

  new_step(7, "Delete the table.")
  if scaffold.exists?(table_name)
    scaffold.delete_table
  end
end
# snippet-end:[ruby.example_code.dynamodb.Scenario_PartiQL_Single]

run_scenario if __FILE__ == $PROGRAM_NAME
