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
require_relative("dynamodb_basics")
require_relative("../scaffold")

# Runs the Amazon DynamoDB demo.
# @return [Nil]
def run_scenario
  banner
  puts "######################################################################################################".yellow
  puts "#                                                                                                    #".yellow
  puts "#                                          EXAMPLE CODE DEMO:                                        #".yellow
  puts "#                                           Amazon DynamoDB                                          #".yellow
  puts "#                                                                                                    #".yellow
  puts "######################################################################################################".yellow
  puts ""
  puts "You have launched a demo of Amazon DynamoDB using the AWS for Ruby v3 SDK. Over the next 60 seconds, it will"
  puts "do the following:"
  puts "    1. Create a new DynamoDB table."
  puts "    2. Write batch data into the table"
  puts "    3. Update an item in the table"
  puts "    4. Delete an item in the table."
  puts "    5. Query the table using PartiQL."
  puts "    6. Scan the table using PartiQL."
  puts "    7. Destroy the DynamoDB table."
  puts ""

  confirm_begin
  billing
  security
  puts "\e[H\e[2J"

  # snippet-start:[ruby.example_code.dynamodb.Scenario_Basics]
  table_name = "doc-example-table-movies-#{rand(10**4)}"
  scaffold = Scaffold.new(table_name)
  dynamodb_wrapper = DynamoDBBasics.new(table_name)

  new_step(1, "Create a new DynamoDB table if none already exists.")
  unless scaffold.exists?(table_name)
    puts("\nNo such table: #{table_name}. Creating it...")
    scaffold.create_table(table_name)
    print "Done!\n".green
  end

  new_step(2, "Add a new record to the DynamoDB table.")
  my_movie = {}
  my_movie[:title] = CLI::UI::Prompt.ask("Enter the title of a movie to add to the table. E.g. The Matrix")
  my_movie[:year] = CLI::UI::Prompt.ask("What year was it released? E.g. 1989").to_i
  my_movie[:rating] = CLI::UI::Prompt.ask("On a scale of 1 - 10, how do you rate it? E.g. 7").to_i
  my_movie[:plot] = CLI::UI::Prompt.ask("Enter a brief summary of the plot. E.g. A man awakens to a new reality.")
  dynamodb_wrapper.add_item(my_movie)
  puts("\nNew record added:")
  puts JSON.pretty_generate(my_movie).green
  print "Done!\n".green

  new_step(3, "Update a record in the DynamoDB table.")
  my_movie[:rating] = CLI::UI::Prompt.ask("Let's update the movie you added with a new rating, e.g. 3:").to_i
  response = dynamodb_wrapper.update_item(my_movie)
  puts("Updated '#{my_movie[:title]}' with new attributes:")
  puts JSON.pretty_generate(response).green
  print "Done!\n".green

  new_step(4, "Get a record from the DynamoDB table.")
  puts("Searching for #{my_movie[:title]} (#{my_movie[:year]})...")
  response = dynamodb_wrapper.get_item(my_movie[:title], my_movie[:year])
  puts JSON.pretty_generate(response).green
  print "Done!\n".green

  new_step(5, "Write a batch of items into the DynamoDB table.")
  download_file = "moviedata.json"
  puts("Downloading movie database to #{download_file}...")
  movie_data = fetch_movie_data(download_file)
  puts("Writing movie data from #{download_file} into your table...")
  scaffold.write_batch(movie_data)
  puts("Records added: #{movie_data.length}.")
  print "Done!\n".green

  new_step(5, "Query for a batch of items by key.")
  loop do
    release_year = CLI::UI::Prompt.ask("Enter a year between 1972 and 2018, e.g. 1999:").to_i
    results = dynamodb_wrapper.query_items(release_year)
    if results.any?
      puts("There were #{results.length} movies released in #{release_year}:")
      results.each do |movie|
        puts("\t #{movie["title"]}").green
      end
      break
    else
      continue = CLI::UI::Prompt.ask("Found no movies released in #{release_year}! Try another year? (y/n)")
      break if !continue.eql?("y")
    end
  end
  print "\nDone!\n".green

  new_step(6, "Scan for a batch of items using a filter expression.")
  years = {}
  years[:start] = CLI::UI::Prompt.ask("Enter a starting year between 1972 and 2018:")
  years[:end] = CLI::UI::Prompt.ask("Enter an ending year between 1972 and 2018:")
  releases = dynamodb_wrapper.scan_items(years)
  if !releases.empty?
    puts("Found #{releases.length} movies.")
    count = Question.ask(
      "How many do you want to see? ", method(:is_int), in_range(1, releases.length))
    puts("Here are your #{count} movies:")
    releases.take(count).each do |release|
      puts("\t#{release["title"]}")
    end
  else
    puts("I don't know about any movies released between #{years[:start]} "\
         "and #{years[:end]}.")
  end
  print "\nDone!\n".green

  new_step(7, "Delete an item from the DynamoDB table.")
  answer = CLI::UI::Prompt.ask("Do you want to remove '#{my_movie[:title]}'? (y/n) ")
  if answer.eql?("y")
    dynamodb_wrapper.delete_item(my_movie[:title], my_movie[:year])
    puts("Removed '#{my_movie[:title]}' from the table.")
    print "\nDone!\n".green
  end

  new_step(8, "Delete the DynamoDB table.")
  answer = CLI::UI::Prompt.ask("Delete the table? (y/n)")
  if answer.eql?("y")
    dynamodb_wrapper.delete_table
    puts("Deleted #{table_name}.")
  else
    puts("Don't forget to delete the table when you're done!")
  end
  print "\nThanks for watching!\n".green
rescue Aws::Errors::ServiceError
  puts("Something went wrong with the demo.")
rescue Errno::ENOENT
  true
end
# snippet-end:[ruby.example_code.dynamodb.Scenario_Basics]

run_scenario if __FILE__ == $PROGRAM_NAME
