/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
This scenario demonstrates how to:
    - Create a table that can hold movie data.
    - Write movie data to the table from a sample JSON file.
    - Put, get, and update a single movie in the table.
    - Update a movie.
    - Query for movies that were released in a given year.
    - Scan for movies that were released in a range of years.
    - Delete a movie from the table.
    - Delete the table.


Running the code:
1. Download 'movies.json' from https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Js.02.html,
   and put it in the same folder as the example.
2. Update the AWS Region in '../libs/ddbClient.js'.
3. Run the following at the command line:
   node dynamodb_basics.js <table_name> <newMovieName> <newMovieYear> <existingMovieName> <existingMovieYear> <newMovieRank> <newMoviePlot>

   For example, node dynamodb_basics.js myNewTable myMovieName 2022 'This Is the End' 2013 200 'A coder cracks code...'

// snippet-start:[javascript.dynamodb_scenarios.dynamodb_basics]
*/
import fs from "fs";
// A practical functional library used to split the data into segments.
import * as R from "ramda";
import { ddbClient } from "../libs/ddbClient.js";
import {
  CreateTableCommand,
  DeleteTableCommand,
} from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";
import {
  PutCommand,
  GetCommand,
  UpdateCommand,
  BatchWriteCommand,
  DeleteCommand,
  ScanCommand,
  QueryCommand,
} from "@aws-sdk/lib-dynamodb";

if (process.argv.length < 6) {
  console.log(
    "Usage: node dynamodb_basics.js <tableNaame> <newMovieName> <newMovieYear> <existingMovieName> <existingMovieYear> <newMovieRank> <newMoviePlot>\n" +
      "Example: node dynamodb_basics.js newmoviesbrmur newmoviename 2025 200 'MOVIE PLOT DETAILS'"
  );
}

// Helper function to delay running the code while the AWS service calls wait for responses.
function wait(ms) {
  var start = Date.now();
  var end = start;
  while (end < start + ms) {
    end = Date.now();
  }
}
// Set the parameters.
const tableName = process.argv[2];
const newMovieName = process.argv[3];
const newMovieYear = parseInt(process.argv[4]); // parseInt() converts the string into a number.
const existingMovieName = process.argv[5];
const existingMovieYear = parseInt(process.argv[6]);
const newMovieRank = parseInt(process.argv[7]);
const newMoviePlot = process.argv[8];

export const run = async (
  tableName,
  newMovieName,
  newMovieYear,
  existingMovieName,
  existingMovieYear,
  newMovieRank,
  newMoviePlot
) => {
  try {
    console.log("Creating table ...");
    // Set the parameters.
    const params = {
      AttributeDefinitions: [
        {
          AttributeName: "title",
          AttributeType: "S",
        },
        {
          AttributeName: "year",
          AttributeType: "N",
        },
      ],
      KeySchema: [
        {
          AttributeName: "title",
          KeyType: "HASH",
        },
        {
          AttributeName: "year",
          KeyType: "RANGE",
        },
      ],
      ProvisionedThroughput: {
        ReadCapacityUnits: 5,
        WriteCapacityUnits: 5,
      },
      TableName: tableName,
    };
    const data = await ddbClient.send(new CreateTableCommand(params));
    console.log("Waiting for table to be created...");
    wait(10000);
    console.log(
      "Table created. Table name is ",
      data.TableDescription.TableName
    );
    try {
      const params = {
        TableName: tableName,
        Item: {
          title: newMovieName,
          year: newMovieYear,
        },
      };
      console.log("Adding movie...");
      await ddbDocClient.send(new PutCommand(params));
      console.log("Success - single movie added.");
      try {
        // Before you run this example, download 'movies.json' from https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Js.02.html,
        // and put it in the same folder as the example.
        // Get the movie data parse to convert into a JSON object.
        const allMovies = JSON.parse(fs.readFileSync("moviedata.json", "utf8"));
        // Split the table into segments of 25.
        const dataSegments = R.splitEvery(25, allMovies);
        // Loop batch write operation 10 times to upload 250 items.
        console.log("Writing movies in batch to table...");
        for (let i = 0; i < 10; i++) {
          const segment = dataSegments[i];
          for (let j = 0; j < 25; j++) {
            const params = {
              RequestItems: {
                [tableName]: [
                  {
                    // Destination Amazon DynamoDB table name.
                    PutRequest: {
                      Item: {
                        year: segment[j].year,
                        title: segment[j].title,
                        info: segment[j].info,
                      },
                    },
                  },
                ],
              },
            };
            ddbDocClient.send(new BatchWriteCommand(params));
          }
        }
        wait(20000);
        console.log("Success, movies written to table.");
        try {
          const params = {
            TableName: tableName,
            Key: {
              title: existingMovieName,
              year: existingMovieYear,
            },
            // Define expressions for the new or updated attributes.
            ProjectionExpression: "#r",
            ExpressionAttributeNames: { "#r": "rank" },
            UpdateExpression: "set info.plot = :p, info.#r = :r",
            ExpressionAttributeValues: {
              ":p": newMoviePlot,
              ":r": newMovieRank,
            },
            ReturnValues: "ALL_NEW",
          };
          console.log("Updating a single movie...");
          await ddbClient.send(new UpdateCommand(params));
          console.log("Success - movie updated.");
          try {
            console.log("Getting movie....");
            const params = {
              TableName: tableName,
              Key: {
                title: existingMovieName,
                year: existingMovieYear,
              },
            };
            const data = await ddbDocClient.send(new GetCommand(params));
            console.log("Success getting item. Item details :", data.Item);
            try {
              const params = {
                TableName: tableName,

                Key: {
                  title: newMovieName,
                  year: newMovieYear,
                },
              };
              await ddbDocClient.send(new DeleteCommand(params));
              console.log("Success - movie deleted.");
              try {
                console.log("Scanning table....");
                const params = {
                  TableName: tableName,
                  ProjectionExpression: "#r, #y, title",
                  ExpressionAttributeNames: { "#r": "rank", "#y": "year" },
                  FilterExpression: "title = :t and #y = :y and info.#r = :r",
                  ExpressionAttributeValues: {
                    ":r": newMovieRank,
                    ":y": existingMovieYear,
                    ":t": existingMovieName,
                  },
                };
                const data = await ddbClient.send(new ScanCommand(params));
                // Loop through and parse the response.
                for (let i = 0; i < data.Items.length; i++) {
                  console.log(
                    "Scan successful. Items with rank of " +
                      newMovieRank +
                      " include\n" +
                      "Year = " +
                      data.Items[i].year +
                      " Title = " +
                      data.Items[i].title
                  );
                }
                try {
                  const params = {
                    ExpressionAttributeNames: { "#r": "rank", "#y": "year" },
                    ProjectionExpression: "#r, #y, title",
                    TableName: tableName,
                    UpdateExpression: "set #r = :r, title = :t, #y = :y",
                    ExpressionAttributeValues: {
                      ":t": existingMovieName,
                      ":y": existingMovieYear,
                      ":r": newMovieRank,
                    },
                    KeyConditionExpression: "title = :t and #y = :y",
                    FilterExpression: "info.#r = :r",
                  };

                  console.log("Querying table...");
                  const data = await ddbDocClient.send(
                    new QueryCommand(params)
                  );
                  // Loop through and parse the response.
                  for (let i = 0; i < data.Items.length; i++) {
                    console.log(
                      "Query successful. Items with rank of " +
                        newMovieRank +
                        " include\n" +
                        "Year = " +
                        data.Items[i].year +
                        " Title = " +
                        data.Items[i].title
                    );
                  }
                  try {
                    console.log("Deleting a movie...");
                    const params = {
                      TableName: tableName,
                      Key: {
                        title: existingMovieName,
                        year: existingMovieYear,
                      },
                    };
                    await ddbDocClient.send(
                      new DeleteCommand(params)
                    );
                    console.log("Success - item deleted");
                    try {
                      console.log("Deleting the table...");
                      const params = {
                        TableName: tableName,
                      };
                      await ddbDocClient.send(
                        new DeleteTableCommand(params)
                      );
                      console.log("Success, table deleted.");
                      return "Run successfully"; // For unit tests.
                    } catch (err) {
                      console.log("Error deleting table. ", err);
                    }
                  } catch (err) {
                    console.log("Error deleting movie. ", err);
                  }
                } catch (err) {
                  console.log("Error querying table. ", err);
                }
              } catch (err) {
                console.log("Error scanning table. ", err);
              }
            } catch (err) {
              console.log("Error deleting movie. ", err);
            }
          } catch (err) {
            console.log("Error getting item. ", err);
          }
        } catch (err) {
          console.log("Error adding movies by batch. ", err);
        }
      } catch (err) {
        console.log("Error updating item. ", err);
      }
    } catch (err) {
      console.log("Error adding a single item. ", err);
    }
  } catch (err) {
    console.log("Error creating table. ", err);
  }
};
run(
  tableName,
  newMovieName,
  newMovieYear,
  existingMovieName,
  existingMovieYear,
  newMovieRank,
  newMoviePlot
);
// snippet-end:[javascript.dynamodb_scenarios.dynamodb_basics]
