# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "aws-sdk-dynamodb"
require "json"
require "open-uri"
require "pp"
require "zip"
require_relative "../scaffold"

# snippet-start:[ruby.example_code.ruby.DynamoDBPartiQLBatch.full]
# snippet-start:[ruby.example_code.ruby.DynamoDBPartiQLBatch.decl]
class DynamoDBPartiQLBatch

  attr_reader :dynamo_resource
  attr_reader :table

  def initialize(table_name)
    client = Aws::DynamoDB::Client.new(region: "us-east-1")
    @dynamodb = Aws::DynamoDB::Resource.new(client: client)
    @table = @dynamodb.table(table_name)
  end
  # snippet-end:[ruby.example_code.ruby.DynamoDBPartiQLBatch.decl]

  # snippet-start:[ruby.example_code.dynamodb.partiql.batch_read]
  # Selects a batch of items from a table using PartiQL
  #
  # @param batch_titles [Array] Collection of movie titles
  # @return [Aws::DynamoDB::Types::BatchExecuteStatementOutput]
  def batch_execute_select(batch_titles)
    request_items = batch_titles.map do |title, year|
      {
        statement: "SELECT * FROM \"#{@table.name}\" WHERE title=? and year=?",
        parameters: [title, year]
      }
    end
    @dynamodb.client.batch_execute_statement({statements: request_items})
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.batch_read]


  # snippet-start:[ruby.example_code.dynamodb.partiql.batch_write]
  # Deletes a batch of items from a table using PartiQL
  #
  # @param batch_titles [Array] Collection of movie titles
  # @return [Aws::DynamoDB::Types::BatchExecuteStatementOutput]
  def batch_execute_write(batch_titles)
    request_items = batch_titles.map do |title, year|
      {
        statement: "DELETE FROM \"#{@table.name}\" WHERE title=? and year=?",
        parameters: [title, year]
      }
    end
    @dynamodb.client.batch_execute_statement({statements: request_items})
  end
  # snippet-end:[ruby.example_code.dynamodb.partiql.batch_write]
end
# snippet-end:[ruby.example_code.ruby.DynamoDBPartiQLBatch.full]
