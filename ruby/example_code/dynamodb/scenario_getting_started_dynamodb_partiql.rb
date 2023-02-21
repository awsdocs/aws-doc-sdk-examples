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
require 'pry'
require_relative("../../helpers/disclaimers")
require_relative("../../helpers/decorators")
require_relative("dynamodb_basics")
require_relative("dynamodb_partiql_basics")

@logger = Logger.new($stdout)
@logger.level = Logger::WARN

# snippet-start:[ruby.example_code.dynamodb.Scenario_GettingStartedDynamoDBPartiQL]

# Runs the DynamoDB getting started demo.
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
  puts "    1. Get items from a table (single and batch) using PartiQL"
  puts "    2. Update items in a table (single and batch) using PartiQL"
  puts "    3. Delete items from a table (single and batch) using PartiQL"
  puts "    4. Insert items into a table (single and batch) using PartiQL"
  puts ""

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  table_name = "doc-example-table-movies-partiql-#{rand(10**4)}"
  scaffold = Scaffold.new(table_name)
  sdk = DynamoDBPartiQLBasics.new(table_name)

  new_step(1, "Create a new DynamoDB table if none already exists.")
  unless scaffold.exists?(table_name)
    puts("\nNo such table: #{table_name}. Creating it...")
    scaffold.create_table(table_name)
    print "Done!\n".green
  end

  new_step(2, "Write a batch of famous movies into the DynamoDB table.")
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
  puts "Let's correct the rating on The Big Lebowski to 10.0"
  sdk.update_rating_by_title('The Big Lebowski', 1998, 10.0)
  print "\nDone!\n".green

  new_step(5, "Delete a single item from the movies table.")
  puts "Let's delete The Silence of the Lambs because it's just too scary."
  sdk.delete_item_by_title('The Silence of the Lambs', 1991)
  print "\nDone!\n".green

  new_step(6, "Insert a new item into the movies table.")
  puts "Let's create a less-scary movie called The Prancing of the Lambs."
  sdk.insert_item("The Prancing of the Lambs", 2005, 'A movie about happy livestock.', 5.0)
  print "\nDone!\n".green

  new_step(7, "Select a batch of items from the movies table.")
  puts "Let's select some popular movies for side-by-side comparison."
  response = sdk.batch_execute_select(['Star Wars', 'The Big Lebowski', 'The Prancing of the Lambs'])
  puts("Items selected: #{response.items.length}\n")
  print "\nDone!\n".green

  new_step(8, "Delete a batch of items from the movies table.")
  sdk.batch_execute_write([["Mean Girls", 2004], ['Goodfellas', 1977], ['The Prancing of the Lambs', 2005]])
  print "\nDone!\n".green

  new_step(9, "Delete the table.")
  if scaffold.exists?(table_name)
    scaffold.delete_table
  end
end
# snippet-end:[ruby.example_code.dynamodb.Scenario_GettingStartedDynamoDBPartiQL

run_scenario if __FILE__ == $PROGRAM_NAME