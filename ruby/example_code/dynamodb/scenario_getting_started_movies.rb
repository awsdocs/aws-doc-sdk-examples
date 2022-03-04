# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to use the AWS SDK for Ruby with Amazon DynamoDB to
# create and use a table that stores data about movies.
#
# 1. Load the table with data from a JSON file.
# 2. Perform basic operations like adding, getting, and updating data for individual movies.
# 3. Use conditional expressions to update movie data only when it meets certain criteria.
# 4. Query and scan the table to retrieve movie data that meets varying criteria.

# snippet-start:[ruby.example_code.dynamodb.helper.Movies]
require "aws-sdk-dynamodb"
require "json"
require "open-uri"
require "pp"
require "zip"
require_relative "question"

# Encapsulates an Amazon DynamoDB table of movie data.
class Movies
  attr_reader :dynamo_resource
  attr_reader :table

  def initialize(dynamo_resource)
    @dynamo_resource = dynamo_resource
  end

  # snippet-start:[ruby.example_code.dynamodb.DescribeTable]
  # Determines whether a table exists. As a side effect, stores the table in
  # a member variable.
  #
  # @param table_name [String] The name of the table to check.
  # @return [Boolean] True when the table exists; otherwise, False.
  def exists?(table_name)
    table = Aws::DynamoDB::Table.new(table_name)
    table.load
    @table = table
  rescue Aws::DynamoDB::Errors::ResourceNotFoundException
    puts("Table #{table_name} doesn't exist. Let's create it.")
    false
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't check for existence of #{table_name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    !@table.nil?
  end
  # snippet-end:[ruby.example_code.dynamodb.DescribeTable]

  # snippet-start:[ruby.example_code.dynamodb.CreateTable]
  # Creates an Amazon DynamoDB table that can be used to store movie data.
  # The table uses the release year of the movie as the partition key and the
  # title as the sort key.
  #
  # @param table_name [String] The name of the table to create.
  # @return [Aws::DynamoDB::Table] The newly created table.
  def create_table(table_name)
    @table = @dynamo_resource.create_table(
      table_name: table_name,
      key_schema: [
        {attribute_name: "year", key_type: "HASH"},  # Partition key
        {attribute_name: "title", key_type: "RANGE"}  # Sort key
      ],
      attribute_definitions: [
        {attribute_name: "year", attribute_type: "N"},
        {attribute_name: "title", attribute_type: "S"}
      ],
      provisioned_throughput: {read_capacity_units: 10, write_capacity_units: 10})
    @dynamo_resource.client.wait_until(:table_exists, table_name: table_name)
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create table #{table_name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    @table
  end
  # snippet-end:[ruby.example_code.dynamodb.CreateTable]

  # snippet-start:[ruby.example_code.dynamodb.BatchWriteItem]
  # Fills an Amazon DynamoDB table with the specified data. Items are sent in
  # batches of 25 until all items are written.
  #
  # @param movies [Enumerable] The data to put in the table. Each item must contain at least
  #                            the keys required by the schema that was specified when the
  #                            table was created.
  def write_batch(movies)
    index = 0
    slice_size = 25
    while index < movies.length
      movie_items = []
      movies[index, slice_size].each do |movie|
        movie_items.append({put_request: { item: movie }})
      end
      @dynamo_resource.batch_write_item({request_items: { @table.name => movie_items }})
      index += slice_size
    end
  rescue Aws::Errors::ServiceError => e
    puts(
      "Couldn't load data into table #{@table.name}. Here's why:")
      puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.BatchWriteItem]

  # snippet-start:[ruby.example_code.dynamodb.PutItem]
  # Adds a movie to the table.
  #
  # @param title [String] The title of the movie.
  # @param year [Integer] The release year of the movie.
  # @param plot [String] The plot summary of the movie.
  # @param rating [Float] The quality rating of the movie.
  def add_movie(title:, year:, plot:, rating:)
    @table.put_item(
      item: {
        "year" => year,
        "title" => title,
        "info" => {"plot" => plot, "rating" => rating}})
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't add movie #{title} to table #{@table.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.PutItem]

  # snippet-start:[ruby.example_code.dynamodb.GetItem]
  # Gets movie data from the table for a specific movie.
  #
  # @param title [String] The title of the movie.
  # @param year [Integer] The release year of the movie.
  # @return [Hash] The data about the requested movie.
  def get_movie(title, year)
    response = @table.get_item(key: {"year" => year, "title" => title})
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't get movie #{title} from table #{@table.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    response.item
  end
  # snippet-end:[ruby.example_code.dynamodb.GetItem]

  # snippet-start:[ruby.example_code.dynamodb.UpdateItem.UpdateExpression]
  # Updates rating and plot data for a movie in the table.
  #
  # @param title [String] The title of the movie to update.
  # @param year [Int] The release year of the movie to update.
  # @param rating [Float] The updated rating to give the movie.
  # @param plot [String] The updated plot summary to give the movie.
  # @return [Hash] The fields that were updated, with their new values.
  def update_movie(title:, year:, rating:, plot:)
    response = @table.update_item(
      key: {"year" => year, "title" => title},
      update_expression: "set info.rating=:r, info.plot=:p",
      expression_attribute_values: { ":r" => rating, ":p" => plot },
      return_values: "UPDATED_NEW")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't update movie #{title} in table #{@table.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    response.attributes
  end
  # snippet-end:[ruby.example_code.dynamodb.UpdateItem.UpdateExpression]

  # snippet-start:[ruby.example_code.dynamodb.Query]
  # Queries for movies that were released in the specified year.
  #
  # @param year [Integer] The year to query.
  # @return [Array] The list of movies that were released in the specified year.
  def query_movies(year)
    response = @table.query(
      key_condition_expression: "#yr = :year",
      expression_attribute_names: {"#yr" => "year"},
      expression_attribute_values: {":year" => year})
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't query for movies released in #{year}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    response.items
  end
  # snippet-end:[ruby.example_code.dynamodb.Query]

  # snippet-start:[ruby.example_code.dynamodb.Scan]
  # Scans for movies that were released in a range of years.
  # Uses a projection expression to return a subset of data for each movie.
  #
  # @param year_range [Hash] The range of years to retrieve.
  # @return [Array] The list of movies released in the specified years.
  def scan_movies(year_range)
    movies = []
    scan_hash = {
      filter_expression: "#yr between :start_yr and :end_yr",
      projection_expression: "#yr, title, info.rating",
      expression_attribute_names: {"#yr" => "year"},
      expression_attribute_values: {
        ":start_yr" => year_range[:start], ":end_yr" => year_range[:end]}
    }
    done = false
    start_key = nil
    until done
      scan_hash[:exclusive_start_key] = start_key unless start_key.nil?
      response = @table.scan(scan_hash)
      movies.concat(response.items) unless response.items.nil?
      start_key = response.last_evaluated_key
      done = start_key.nil?
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't scan for movies. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    movies
  end
  # snippet-end:[ruby.example_code.dynamodb.Scan]

  # snippet-start:[ruby.example_code.dynamodb.DeleteItem]
  # Deletes a movie from the table.
  #
  # @param title [String] The title of the movie to delete.
  # @param year [Integer] The release year of the movie to delete.
  def delete_movie(title, year)
    @table.delete_item(key: {"year" => year, "title" => title})
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't delete movie #{title}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.DeleteItem]

  # snippet-start:[ruby.example_code.dynamodb.DeleteTable]
  # Deletes the table.
  def delete_table
    @table.delete
    @table = nil
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't delete table. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.DeleteTable]
end
# snippet-end:[ruby.example_code.dynamodb.helper.Movies]

# snippet-start:[ruby.example_code.dynamodb.helper.get_sample_movie_data]
# Gets sample movie data, either from a local file or by first downloading it from
# the Amazon DynamoDB Developer Guide.
#
# @param movie_file_name [String] The local file name where the movie data is stored in JSON format.
# @return [Hash] The movie data as a Hash.
def get_sample_movie_data(movie_file_name)
  if !File.file?(movie_file_name)
    puts("Downloading #{movie_file_name}...")
    movie_content = URI.open(
      "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip"
    )
    movie_json = ""
    Zip::File.open_buffer(movie_content) do |zip|
      zip.each do |entry|
        movie_json = entry.get_input_stream.read
      end
    end
  else
    movie_json = File.read(movie_file_name)
  end
  movie_data = JSON.parse(movie_json)
  # The sample file lists over 4000 movies. This returns only the first 250.
  movie_data.slice(0, 250)
rescue Errno::ENOENT
  puts("File #{movie_file_name} not found. Before you can run this demo, you must "\
       "download the file. For instructions, see the README.")
  raise
end
# snippet-end:[ruby.example_code.dynamodb.helper.get_sample_movie_data]

# snippet-start:[ruby.example_code.dynamodb.Scenario_GettingStartedMovies]
# Runs the DynamoDB getting started demo.
#
# @param movies [Movies] A wrapper class initialized with a DynamoDB resource.
# @param table_name [String] The name to give the movie table.
# @param movie_file_name [String] The name of a file that contains movie data in JSON
#                                 format. This data is loaded into the movie table
#                                 as part of the demo.
def run_scenario(movies, table_name, movie_file_name)
  puts("-" * 88)
  puts("Welcome to the DynamoDB getting started demo.")
  puts("-" * 88)

  movies_exists = movies.exists?(table_name)
  unless movies_exists
    puts("\nCreating table #{table_name}...")
    movies.create_table(table_name)
    puts("\nCreated table #{movies.table.name}.")
  end

  my_movie = {}
  my_movie[:title] = Question.ask("Enter the title of a movie to add to the table: ")
  my_movie[:year] = Question.ask("What year was it released? ", method(:is_int))
  my_movie[:rating] = Question.ask(
    "On a scale of 1 - 10, how do you rate it? ", method(:is_float), in_range(1, 10)
  )
  my_movie[:plot] = Question.ask("Summarize the plot for me: ")

  movies.add_movie(**my_movie)
  puts("\nAdded '#{my_movie[:title]}' to '#{movies.table.name}'.")
  puts("-" * 88)

  puts("Let's update your movie. You rated it #{my_movie[:rating]}.")
  my_movie[:rating] = Question.ask("What new rating would you give it? ",
    method(:is_float), in_range(1, 10))
  puts("You summarized the plot as '#{my_movie[:plot]}'.")
  my_movie[:plot] = Question.ask("What would you say now? ")
  updated = movies.update_movie(**my_movie)
  puts("Updated '#{my_movie[:title]}' with new attributes:")
  pp(updated)
  puts("-" * 88)

  unless movies_exists
    movie_data = get_sample_movie_data(movie_file_name)
    puts("Reading data from '#{movie_file_name}' into your table.")
    movies.write_batch(movie_data)
    puts("Wrote #{movie_data.length} movies into #{movies.table.name}.")
    puts("-" * 88)
  end

  title = "The Lord of the Rings: The Fellowship of the Ring"
  if Question.ask("Let's move on. Do you want to get info about '#{title}'? (y/n) ",
         method(:is_yesno))
    movie = movies.get_movie(title, 2001)
    puts("\nHere's what I found:")
    pp(movie)
    puts("-" * 88)
  end

  ask_for_year = true
  puts("Let's get a list of movies released in a given year.")
  while ask_for_year
    release_year = Question.ask(
      "Enter a year between 1972 and 2018: ", method(:is_int), in_range(1972, 2018))
    releases = movies.query_movies(release_year)
    if !releases.empty?
      puts("There were #{releases.length} movies released in #{release_year}:")
      releases.each do |release|
        puts("\t#{release["title"]}")
        ask_for_year = false
      end
    else
      puts("I don't know about any movies released in #{release_year}!")
      ask_for_year = Question.ask("Try another year? (y/n) ", method(:is_yesno))
      puts("-" * 88)
    end
  end

  years = {}
  years[:start] = Question.ask(
    "Let's scan for movies released in a range of years. Enter a year: ",
    method(:is_int), in_range(1972, 2018))
  years[:end] = Question.ask(
    "Now enter another year: ", method(:is_int), in_range(1972, 2018))
  releases = movies.scan_movies(years)
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
  puts("-" * 88)
  end

  puts("Let's remove your movie from the table.")
  if Question.ask(
    "Do you want to remove '#{my_movie[:title]}'? (y/n) ", method(:is_yesno))
    movies.delete_movie(my_movie[:title], my_movie[:year])
    puts("Removed '#{my_movie[:title]}' from the table.")
    puts("-" * 88)
  end

  if Question.ask("Delete the table? (y/n) ", method(:is_yesno))
    movies.delete_table
    puts("Deleted #{table_name}.")
  else
    puts("Don't forget to delete the table when you're done or you might incur "\
      "charges on your account.")
  end

  puts("\nThanks for watching!")
  puts("-" * 88)
rescue Aws::Errors::ServiceError
  puts("Something went wrong with the demo.")
rescue Errno::ENOENT
  true
end

run_scenario(
  Movies.new(Aws::DynamoDB::Resource.new),
"doc-example-table-movies", "moviedata.json",
  ) if $PROGRAM_NAME == __FILE__
# snippet-end:[ruby.example_code.dynamodb.Scenario_GettingStartedMovies]
