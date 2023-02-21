# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.example_code.dynamodb.helper.DynamoDBBasics]
require "aws-sdk-dynamodb"
require "json"
require "open-uri"
require "pp"
require "zip"
require_relative 'scaffold'

class DynamoDBPartiQLBasics
  def initialize(table_name)
    @dynamodb = Aws::DynamoDB::Resource.new
    @table_name = table_name
  end

  # Note: to perform more fine-grained selects,
  # use the Client.query instance method instead
  def select_item_by_title(title)
    request = {
      statement: "SELECT * FROM \"#{@table_name}\" WHERE title=?",
      parameters: [title]
    }
    @dynamodb.client.execute_statement(request)
  end

  def update_rating_by_title(title, year, new_rating)
    request = {
      statement: "UPDATE \"#{@table_name}\" SET info.rating=? WHERE title=? and year=?",
      parameters: [{ "N": new_rating }, title, year]
    }
    @dynamodb.client.execute_statement(request)
  end

  def delete_item_by_title(title, year)
    request = {
      statement: "DELETE FROM \"#{@table_name}\" WHERE title=? and year=?",
      parameters: [title, year]
    }
    @dynamodb.client.execute_statement(request)
  end

  def insert_item(title, year, plot, rating)
    request = {
      statement: "INSERT INTO \"#{@table_name}\" VALUE {'title': ?, 'year': ?, 'info': ?}",
      parameters: [title, year, {'plot': plot, 'rating': rating}]
    }
    @dynamodb.client.execute_statement(request)
  end

  def batch_execute_select(batch_titles)
    request_items = batch_titles.map do |title|
      {
        statement: "SELECT * FROM \"#{@table_name}\" WHERE title=?",
        parameters: [title]
      }
    end
    @dynamodb.client.batch_execute_statement({statements: request_items})
  end

  def batch_execute_write(batch_titles)
    request_items = batch_titles.map do |title, year|
      {
        statement: "DELETE FROM \"#{@table_name}\" WHERE title=? and year=?",
        parameters: [title, year]
      }
    end
    binding.pry
    @dynamodb.client.batch_execute_statement({statements: request_items})
  end
end
