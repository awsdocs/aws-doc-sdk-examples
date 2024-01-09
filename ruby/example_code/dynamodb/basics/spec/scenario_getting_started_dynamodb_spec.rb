# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require "json"
require "rspec"
require_relative "../dynamodb_basics"
require_relative "../../scaffold"

describe DynamoDBBasics do
  context "DynamoDBWrapper" do
    table_name = "doc-example-table-movies-#{rand(10**4)}"
    scaffold = Scaffold.new(table_name)
    sdk = DynamoDBBasics.new(table_name)

    it "Create a new DynamoDB table", integ: true do
      scaffold.create_table(table_name)
      expect(scaffold.exists?(table_name)).to be_truthy
    end

    it "Write a batch of famous movies into the DynamoDB table", integ: true do
      movie_data = scaffold.fetch_movie_data("moviedata.json")
      scaffold.write_batch(movie_data)
      expect(movie_data.length).to be > 200
    end

    it "Get a record from the DynamoDB table", integ: true do
      response = sdk.get_item("12 Years a Slave", 2013)
      expect(response.item["info"]["rating"].to_i).to eq(7)
    end

    it "Add a new record to the DynamoDB table", integ: true do
      test_record = { title: "The Matrix 5", year: 2023, rating: 3, plot: "A man awakens to yet another new reality." }
      response = sdk.add_item(test_record)
      expect(response).not_to be_nil
    end

    it "Update a record in the DynamoDB table", integ: true do
      test_record = { title: "12 Years a Slave", year: 2013, rating: 8, plot: "A man awakens to a new reality." }
      sdk.update_item(test_record)
      response = sdk.get_item("12 Years a Slave", 2013)
      expect(response.item["info"]["rating"].to_i).to eq(8)
    end

    it "Query for a batch of items by key.", integ: true do
      results = sdk.query_items(1999)
      expect(results.count).to be > 1
      results.each do |movie|
        expect(movie["title"]).to be
      end
    end

    it "Scan for a batch of items using a filter expression.", integ: true do
      years = {}
      years[:start] = 1989
      years[:end] = 1990
      releases = sdk.scan_items(years)
      expect(releases.count).to be > 0
      expect(releases[0]["title"]).to be
    end

    it "Deletes DynamoDB table", integ: true do
      if scaffold.exists?(table_name)
        scaffold.delete_table
      end
    end
  end
end
