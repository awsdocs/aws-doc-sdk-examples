# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "aws-sdk-dynamodb"
require "json"
require "open-uri"
require "pp"
require "zip"
require_relative "../scaffold"

# snippet-start:[ruby.example_code.ruby.DynamoDBPartiQLSingle.full]
# snippet-start:[ruby.example_code.ruby.DynamoDBPartiQLSingle.decl]
class DynamoDBPartiQLSingle

  attr_reader :dynamo_resource
  attr_reader :table

  def initialize(table_name)
    client = Aws::DynamoDB::Client.new(region: "us-east-1")
    @dynamodb = Aws::DynamoDB::Resource.new(client: client)
    @table = @dynamodb.table(table_name)
  end
  # snippet-end:[ruby.example_code.ruby.DynamoDBPartiQLSingle.decl]

  # snippet-start:[ruby.example_code.dynamodb.partiql.single_select]
  # Gets a single record from a table using PartiQL.
  # Note: To perform more fine-grained selects,
  # use the Client.query instance method instead.
  #
  # @param title [String] The title of the movie to search.
  # @return [Aws::DynamoDB::Types::ExecuteStatementOutput]
  def select_item_by_title(title)
    request = {
      statement: "SELECT * FROM \"#{@table.name}\" WHERE title=?",
      parameters: [title]
    }
    @dynamodb.client.execute_statement(request)
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.single_select]

  # snippet-start:[ruby.example_code.dynamodb.partiql.single_update]
  # Updates a single record from a table using PartiQL.
  #
  # @param title [String] The title of the movie to update.
  # @param year [Integer] The year the movie was released.
  # @param rating [Float] The new rating to assign the title.
  # @return [Aws::DynamoDB::Types::ExecuteStatementOutput]
  def update_rating_by_title(title, year, rating)
    request = {
      statement: "UPDATE \"#{@table.name}\" SET info.rating=? WHERE title=? and year=?",
      parameters: [{ "N": rating }, title, year]
    }
    @dynamodb.client.execute_statement(request)
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.single_update]


  # snippet-start:[ruby.example_code.dynamodb.partiql.single_delete]
  # Deletes a single record from a table using PartiQL.
  #
  # @param title [String] The title of the movie to update.
  # @param year [Integer] The year the movie was released.
  # @return [Aws::DynamoDB::Types::ExecuteStatementOutput]
  def delete_item_by_title(title, year)
    request = {
      statement: "DELETE FROM \"#{@table.name}\" WHERE title=? and year=?",
      parameters: [title, year]
    }
    @dynamodb.client.execute_statement(request)
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.single_delete]


  # snippet-start:[ruby.example_code.dynamodb.partiql.single_insert]
  # Adds a single record to a table using PartiQL.
  #
  # @param title [String] The title of the movie to update.
  # @param year [Integer] The year the movie was released.
  # @param plot [String] The plot of the movie.
  # @param rating [Float] The new rating to assign the title.
  # @return [Aws::DynamoDB::Types::ExecuteStatementOutput]
  def insert_item(title, year, plot, rating)
    request = {
      statement: "INSERT INTO \"#{@table.name}\" VALUE {'title': ?, 'year': ?, 'info': ?}",
      parameters: [title, year, {'plot': plot, 'rating': rating}]
    }
    @dynamodb.client.execute_statement(request)
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.single_insert]
end
# snippet-end:[ruby.example_code.ruby.DynamoDBPartiQLSingle.full]
