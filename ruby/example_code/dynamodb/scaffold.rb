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

# snippet-start:[ruby.example_code.dynamodb.helper.DynamoDBBasics]
require "aws-sdk-dynamodb"
require "json"
require "open-uri"
require "pp"
require "zip"

# Encapsulates an Amazon DynamoDB table of movie data.
class Scaffold
  attr_reader :dynamo_resource
  attr_reader :table_name
  attr_reader :table
  attr_reader :movie_file_name

  def initialize(table_name)
    @dynamo_resource = Aws::DynamoDB::Resource.new
    @table_name = table_name
    @table = nil
    @logger = Logger.new($stdout)
    @logger.level = Logger::DEBUG
  end

  # snippet-start:[ruby.example_code.dynamodb.DescribeTable]
  # Determines whether a table exists. As a side effect, stores the table in
  # a member variable.
  #
  # @param table_name [String] The name of the table to check.
  # @return [Boolean] True when the table exists; otherwise, False.
  def exists?(table_name)
    @table = @dynamo_resource.table(table_name)
    @logger.debug("Table #{table_name} exists")
    true
  rescue Aws::DynamoDB::Errors::ResourceNotFoundException
    @logger.debug("Table #{table_name} doesn't exist")
    false
  rescue Aws::DynamoDB::Errors::ServiceError => e
    puts("Couldn't check for existence of #{table_name}:\n")
    puts("\t#{e.code}: #{e.message}")
    raise
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
  rescue Aws::DynamoDB::Errors::ServiceError => e
    @logger.error("Failed create table #{table_name}:\n#{e.code}: #{e.message}")
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
      @dynamo_resource.batch_write_item({request_items: { @table_name => movie_items }})
      index += slice_size
    end
  rescue Aws::DynamoDB::Errors::ServiceError => e
    puts(
      "Couldn't load data into table #{@table.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.BatchWriteItem]

  # snippet-start:[ruby.example_code.dynamodb.DeleteTable]
  # Deletes the table.
  def delete_table
    @table.delete
    @table = nil
  rescue Aws::DynamoDB::Errors::ServiceError => e
    puts("Couldn't delete table. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.dynamodb.DeleteTable]

  # snippet-start:[ruby.example_code.dynamodb.helper.get_sample_movie_data]
  # Gets sample movie data, either from a local file or by first downloading it from
  # the Amazon DynamoDB Developer Guide.
  #
  # @param movie_file_name [String] The local file name where the movie data is stored in JSON format.
  # @return [Hash] The movie data as a Hash.
  def fetch_movie_data(movie_file_name)
    if !File.file?(movie_file_name)
      @logger.debug("Downloading #{movie_file_name}...")
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
end
# snippet-end:[ruby.example_code.dynamodb.helper.DynamoDBBasics]

