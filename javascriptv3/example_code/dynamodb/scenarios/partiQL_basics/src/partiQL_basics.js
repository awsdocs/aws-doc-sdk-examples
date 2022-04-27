/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
Scenario demonstrating how to do the following in an Amazon DymamoDB table using PartiQL:
- Get an item (Select)
- Update an item (Update)
- Delete an item (Delete)
- Put an item (Insert)

Running the code:
node partiQL_basics.js <tableName> <movieTitle1> <movieYear1> <producer1>

For example, node partiQL_basics.js Movies 2006 'The Departed' 'New View Films'

// snippet-start:[javascript.dynamodb_scenarios.partiQL_basics]
*/
import fs from "fs";
// A practical functional library used to split the data into segments.
import * as R from "ramda";
import { ddbClient } from "../libs/ddbClient.js";
import { ddbDocClient } from "../libs/ddbDocClient.js";
import { BatchWriteCommand } from "@aws-sdk/lib-dynamodb";
import {
  CreateTableCommand,
  ExecuteStatementCommand,
} from "@aws-sdk/client-dynamodb";
if (process.argv.length < 6) {
  console.log(
    "Usage: node partiQL_basics.js <tableName> <movieYear1> <movieTitle1> <producer1>\n" +
      "Example: node partiQL_basics.js Movies 2006 'The Departed' 'New View Films'"
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

const tableName = process.argv[2];
const movieTitle1 = process.argv[3];
const movieYear1 = process.argv[4];
const producer1 = process.argv[5];

export const run = async (tableName, movieYear1, movieTitle1, producer1) => {
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
          const data = ddbDocClient.send(new BatchWriteCommand(params));
        }
      }
      wait(20000);
      console.log("Success, movies written to table.");
      try {
        const params = {
          Statement: "SELECT * FROM " + tableName + " where title=?",
          Parameters: [{ S: movieTitle1 }],
        };
        console.log("Getting movie....");

        console.log("Statement", params.Statement);
        const data = await ddbDocClient.send(
          new ExecuteStatementCommand(params)
        );
        for (let i = 0; i < data.Items.length; i++) {
          console.log(
            "Success. The query return the following data. Item " + i,
            data.Items[i].year,
            data.Items[i].title,
            data.Items[i].info
          );
        }
        try {
          const params = {
            Statement: "DELETE FROM " + tableName + " where title=? and year=?",
            Parameters: [{ S: movieTitle1 }, { N: movieYear1 }],
          };
          const data = await ddbDocClient.send(
            new ExecuteStatementCommand(params)
          );
          console.log("Success. Item deleted.");
          try {
            const params = {
              Statement:
                "INSERT INTO " + tableName + " value  {'title':?, 'year':?}",
              Parameters: [{ S: movieTitle1 }, { N: movieYear1 }],
            };
            const data = await ddbDocClient.send(
              new ExecuteStatementCommand(params)
            );
            console.log("Success. Item added.");
            try {
              const params = {
                Statement:
                  "UPDATE " +
                  tableName +
                  " SET Producer=? where title=? and year=?",
                Parameters: [
                  { S: producer1 },
                  { S: movieTitle1 },
                  { N: movieYear1 },
                ],
              };

              console.log("Updating a single movie...");
              const data = await ddbDocClient.send(
                new ExecuteStatementCommand(params)
              );
              console.log("Success. Item updated.");
              return "Run successfully"; // For unit tests.
            } catch (err) {
              console.log("Error updating item. ", err);
            }
          } catch (err) {
            console.log("Error adding items to table. ", err);
          }
        } catch (err) {
          console.log("Error deleting movie. ", err);
        }
      } catch (err) {
        console.log("Error getting movie. ", err);
      }
    } catch (err) {
      console.log("Error adding movies by batch. ", err);
    }
  } catch (err) {
    console.log("Error creating table. ", err);
  }
};
run(tableName, movieYear1, movieTitle1, producer1);
// snippet-end:[javascript.dynamodb_scenarios.partiQL_basics]
