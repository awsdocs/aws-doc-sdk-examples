# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require "json"
require "rspec"
require_relative "../partiql_batch"
require_relative "../../scaffold"

describe DynamoDBPartiQLBatch do
  context "DynamoDBPartiQLWrapper" do
    table_name = "doc-example-table-movies-partiql-#{rand(10**4)}"
    scaffold = Scaffold.new(table_name)
    sdk = DynamoDBPartiQLBatch.new(table_name)

    it "Create a new DynamoDB table", integ: true do
      scaffold.create_table(table_name)
      expect(scaffold.exists?(table_name)).to be_truthy
    end

    it "Write a batch of famous movies into the DynamoDB table", integ: true do
      movie_data = scaffold.fetch_movie_data("moviedata.json")
      scaffold.write_batch(movie_data)
      expect(movie_data.length).to be > 200
    end

    it "returns a valid response when given a list of movies", integ: true do
      movies = ["Star Wars", "The Big Lebowski", "The Prancing of the Lambs"]
      response = sdk.batch_execute_select(movies)
      expect(response["responses"]).to be_an(Array)
      expect(response["responses"].count).to eq(movies.length)
      response["responses"].each do |movie_response|
        expect(movie_response).to be_a(Aws::DynamoDB::Types::BatchStatementResponse)
      end
    end

    it "deletes a list of movies", quarantine: true do
      movies = [["Mean Girls", 2004], ["The Prancing of the Lambs", 2005]]
      sdk.batch_execute_write(movies)
      movies.each do |movie|
        response = sdk.select_item_by_title(movie[0])
        expect(response["items"]).to be_empty
      end
    end

    it "deletes DynamoDB table", integ: true do
      if scaffold.exists?(table_name)
        scaffold.delete_table
      end
    end
  end
end
