# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require "json"
require "rspec"
require_relative("../partiql_single")
require_relative("../../scaffold")

describe DynamoDBPartiQLSingle do
  context "DynamoDBPartiQLWrapper" do
    table_name = "doc-example-table-movies-partiql-#{rand(10**4)}"
    scaffold = Scaffold.new(table_name)
    sdk = DynamoDBPartiQLSingle.new(table_name)

    it "Create a new DynamoDB table", integ: true do
      scaffold.create_table(table_name)
      expect(scaffold.exists?(table_name)).to be_truthy
    end

    it "Write a batch of famous movies into the DynamoDB table", integ: true do
      movie_data = scaffold.fetch_movie_data("moviedata.json")
      scaffold.write_batch(movie_data)
      expect(movie_data.length).to be > 200
    end

    it "should return items with given title", integ: true do
      response = sdk.select_item_by_title("Star Wars")
      expect(response.items.length).to be >= 1
      expect(response.items.first["title"]).to eq("Star Wars")
    end

    it "should update the rating for the given title and year", integ: true do
      title = "The Big Lebowski"
      year = 1998
      rating = 10.0
      sdk.update_rating_by_title(title, year, rating)
      response = sdk.select_item_by_title(title)
      updated_item = response.items.find { |item| item["year"] == year }
      expect(updated_item["info"]["rating"]["N"].to_i).to eq(rating)
    end

    it "should delete the item with the given title and year", integ: true do
      title = "The Silence of the Lambs"
      year = 1991
      sdk.delete_item_by_title(title, year)
      response = sdk.select_item_by_title(title)
      deleted_item = response.items.find { |item| item.year == year }
      expect(deleted_item).to be_nil
    end

    it "should add a new item with the given details", integ: true do
      title = "The Prancing of the Lambs"
      year = 2005
      description = "A movie about happy livestock."
      rating = 5.0
      sdk.insert_item(title, year, description, rating)
      response = sdk.select_item_by_title(title)
      new_item = response.items.find { |item| item["year"] == year }
      expect(new_item).not_to be_nil
      expect(new_item["title"]).to eq(title)
      expect(new_item["year"]).to eq(year)
      expect(new_item["info"]).not_to be_nil
    end

    it "deletes DynamoDB table", integ: true do
      if scaffold.exists?(table_name)
        scaffold.delete_table
      end
    end
  end
end
