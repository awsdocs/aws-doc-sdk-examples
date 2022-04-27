/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_batchwriteitems.js demonstrates how to insert multiple items from JSON file to an Amazon DynamoDB table using PartiQL.

Running the code:
node partiql_batchwriteitems.js <table_name>
*/
// snippet-start:[dynamodb.JavaScript.partiQL.writeBatchTableV3]

// Import required AWS SDK clients and commands for Node.js.
import { ddbDocClient } from "../libs/ddbDocClient.js";
import { BatchWriteCommand } from "@aws-sdk/lib-dynamodb";
import * as R from "ramda";
import fs from "fs";

const tableName = process.argv[2];

export const run = async (tableName) => {
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
    console.log("Success. Movies written to table.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run(tableName);
// snippet-end:[dynamodb.JavaScript.partiQL.writeBatchTableV3]
