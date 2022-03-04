/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
writeData.js demonstrates how to use the Amazon DynamoDB document client write data from a JSON file Amazon DynamoDB table.

This example is based on the 'movies.json' file located at https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javascriptv3/example_code/dynamodb/scenarios/dynamodb_basics/src/movies.json .

Inputs (replace in code):
- TABLE_NAME
- MOVIE_NAME
- MOVIE_YEAR
- MOVIE_PLOT
- MOVIE_RANK

Running the code:
node writeData.js
*/
// snippet-start:[dynamodb.JavaScript.movies.scanV3]

import fs from "fs";
import * as R from "ramda";
import { ddbDocClient } from "../libs/ddbDocClient.js";
import { BatchWriteCommand } from "@aws-sdk/lib-dynamodb";

export const writeData = async () => {
  // Get the movie data parse to convert into a JSON object.
  const allMovies = JSON.parse(fs.readFileSync("moviedata.json", "utf8"));
  // Split the table into segments of 25.
  const dataSegments = R.splitEvery(25, allMovies);

  try {
    // Loop batch write operation 10 times to upload 250 items.
    for (let i = 0; i < 10; i++) {
      const segment = dataSegments[i];
      for (let j = 0; j < 25; j++) {
        const params = {
          RequestItems: {
            newmoviesbrmur: [
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
      console.log("Success, table updated.");
    }
  } catch (error) {
    console.log("Error", error);
  }
};
writeData();
// snippet-start:[dynamodb.JavaScript.scenario.basics.updateV3]