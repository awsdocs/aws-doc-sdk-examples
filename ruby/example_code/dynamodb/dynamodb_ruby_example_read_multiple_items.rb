# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Gets an item from a DynamoDB table.]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[get_item method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[dynamodb]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-dynamodb'  # v2: require 'aws-sdk'
require 'rspec'
# Create dynamodb client in us-west-2 region
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

# Enables working with DynamoDB attribute values using hashes, arrays, sets, integers, floats, booleans, and nil.
# Disabling this option requires that all attribute values have their types specified, e.g. `{ s: 'abc' }`
# instead of simply `'abc'`.
@options [Boolean] :simple_attributes (true)
# Causes the client to return stubbed responses. By default fake responses are generated and returned. No HTTP requests
# will be made.
@options [Boolean] :stub_responses (false)
# Initialize all arguments passed to function into an array called args
module Aws
  module DynamoDB
    class ReadMultipleItems
      def initialize(*args)
      super
end
# Read multiple items from the Movies table using a batch of three GetItem requests. The `BatchGetItem` operation
# returns the attributes of one or more items from one or more tables. You identify requested items by primary key.
# Here, the FilmDirector, MonthsInTheatre, and AcademyAwardWinnerStatus attributes are returned from the Movies table.
# The PlayDirector, MonthsInTheatre, and TonyAwardWinnerStatus attributes are returned from the Plays table.
resp  = client.batch_get_item({
request_items: {
    "Movies" => {
        keys: [
            {
                "Year"  => "2015",
                "MovieTitle" => "The Big New Movie",
                "Plot" => "Action",
                "Rating" => "Excellent",
            },
            {
                "Year" => "2009",
                "MovieTitle" => "A Nature Documentary",
                "Plot" => "Documentary Short",
                "Rating" => "Fair",
            },
            {
                "Year" => "1983",
                "MovieTitle" => "An Animated Fairy Tale",
                "Plot" => "Children's Fiction",
                "Rating" => "Good",
            },
        ],
# To parse the response by item, we include the primary key values for the items in our request with the
# projection_expression parameter
        projection_expression: "FilmDirector, MonthsInTheatre, AcademyAwardWinnerStatus",
          },
        },
      })

    "Plays" => {
        keys: [
            {
                "Year" => "2019",
                "PlayTitle" => "King Sear",
                "Plot" => "Tragedy",
                "Rating" => "Poor",
            },
            {
                "Year" => "1913",
                "PlayTitle" => "A Midautumn Night's Dream",
                "Plot" => "Comedy",
                "Rating" => "Excellent",
            },
        ],
        projection_expression: "PlayDirector, WeeksInTheatre, TonyAwardWinnerStatus",
            }

resp.to_h outputs the following:
{
    responses: {
        "Movies" => [
            {
                "FilmDirector" => "John Doe",
                "MonthsInTheatre" => "9",
                "AcademyAwardWinnerStatus" => "Yes",
            },
            {
                "FilmDirector" => "Jane Doe",
                "MonthsInTheatre" => "4",
                "AcademyAwardWinnerStatus" => "No",
            },
            {
                "FilmDirector" => "Mr. Director",
                "MonthsInTheatre" => "2",
                "AcademyAwardWinnerStatus" => "Yes",
            },
        ],
        "Plays" => [
            {
                "PlayDirector" => "Willy Shakes",
                "WeeksInTheatre" => "3",
                "TonyAwardWinnerStatus" => "No",
            },
            {
                "PlayDirector" => "W.E. Shakingspear",
                "WeeksInTheatre" => "23",
                "TonyAwardWinnerStatus" => "Yes",
            },
        ],
       }

begin
# batch_get_item retrieves the attributes of our items from the Movies table and Plays table in parallel to minimize
# response latency.

def batch_get_item(params = {}, options = {})
  req = build_request(:batch_get_item, params)
  req.send_request(options)

# if the requested item is not present in the table, an appropriate error message is returned
  if req.item == nil
    puts 'Could not find the Director, Time in Theatre, nor Award Winner Status'
    exit 0
  end

# Rescuing an error returned from the service (not one generated by the client) with an accompanying error message
rescue  Aws::DynamoDB::Errors::ServiceError => error
  puts 'Unable to find the Director, Time in Theatre, nor Award Winner Status'
  puts error.message
end
