# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../question"
require_relative "../scenario_getting_started_movies"

describe "Amazon DynamoDB scenario getting started movies" do
  title = "Test"
  inputs = %W(#{title} 2022 3.3 Test-plot 5.6 New-plot y 2001 2001 2018 3 y y)

  context "runs against AWS (integration tests)", integ: true do
    it "runs without errors", integ: true do
      dyn_resource = Aws::DynamoDB::Resource.new
      movies = Movies.new(dyn_resource)
      allow(Question).to receive(:gets).and_return(*inputs)

      expect { run_scenario(movies, "doc-example-table-movies", "moviedata.json") }.
        not_to output(/Something went wrong with the demo/).to_stdout
    end
  end

  context "runs using stubs" do
    table_name = "test-table"
    movie_file_name = "spec/.test.moviedata.json"
    movie_data = [{
       "title" => "Test Movie Title 1!", "year" => 2001,
       "info" => {"rating" => 3.3, "plot" => "Long and boring."}
     }, {
       "title" => "Test Movie Title 2!", "year" => 2001,
       "info" => {"rating" => 3.3, "plot" => "Long and boring."}
     }, {
       "title" => "Test Movie Title 3!", "year" => 2001,
       "info" => {"rating" => 3.3, "plot" => "Long and boring."}
     }
    ]
    let(:dyn_resource) { Aws::DynamoDB::Resource.new(stub_responses: true) }
    let(:movies) { Movies.new(dyn_resource) }

    before do
      allow(Question).to receive(:gets).and_return(*inputs)
      dyn_resource.client.stub_responses(:describe_table, {
        "table": {"table_name": table_name, "table_status": "ACTIVE"}
      })

      dyn_resource.client.stub_responses(:query, { "items": movie_data})
      dyn_resource.client.stub_responses(:scan, { "items": movie_data})
    end

    it "runs without errors" do
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(
        match(/Creating table #{table_name}/).
        and match(/Added '#{title}'/).
        and match(/Updated '#{title}'/).
        and match(/There were #{movie_data.count}/).
        and match(/Found #{movie_data.count}/).
        and match(/Removed '#{title}'/).
        and match(/Deleted #{table_name}/)
      ).to_stdout
    end

    it "outputs correct error when create_table fails" do
      dyn_resource.client.stub_responses(:create_table, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when put_item fails" do
      dyn_resource.client.stub_responses(:put_item, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when update_item fails" do
      dyn_resource.client.stub_responses(:update_item, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when batch_write_item fails" do
      dyn_resource.client.stub_responses(:batch_write_item, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when get_item fails" do
      dyn_resource.client.stub_responses(:get_item, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when query fails" do
      dyn_resource.client.stub_responses(:query, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when scan fails" do
      dyn_resource.client.stub_responses(:scan, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_item fails" do
      dyn_resource.client.stub_responses(:delete_item, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end

    it "outputs correct error when delete_table fails" do
      dyn_resource.client.stub_responses(:delete_table, "TestError")
      expect {
        run_scenario(movies, table_name, movie_file_name)
      }.to output(/TestError: stubbed-response-error-message/).to_stdout
    end
  end
end
