/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

// ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide (scheduled for release later in 2020) topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_batchwriteitem_tv.test.js populates the table used for the match query example
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.


Running the code:
node ddb_batchwriteitem_tv.test.js
*/
// snippet-start:[dynamodb.JavaScript.batch.BatchWriterItemTVV3]
// Import required AWS SDK clients and commands for Node.js
import {
  BatchWriteItemCommand,
} from "@aws-sdk/client-dynamodb";
import { ddbClient } from "../libs/ddbClient.js";
// Set the parameters
export const params = {
  RequestItems: {
    EPISODES_TABLE: [
      {
        PutRequest: {
          Item: {
            Season: { N: "1" },
            Episode: { N: "1" },
            Subtitle: { S: "SubTitle1" },
            Title: { S: "Title1" },
          },
        },
      },
      {
        PutRequest: {
          Item: {
            Season: { N: "1" },
            Episode: { N: "2" },
            Subtitle: { S: "SubTitle2" },
            Title: { S: "Title2" },
          },
        },
      },
      {
        PutRequest: {
          Item: {
            Season: { N: "1" },
            Episode: { N: "3" },
            Subtitle: { S: "SubTitle3" },
            Title: { S: "Title3" },
          },
        },
      },
      {
        PutRequest: {
          Item: {
            Season: { N: "1" },
            Episode: { N: "4" },
            Subtitle: { S: "SubTitle4" },
            Title: { S: "Title4" },
          },
        },
      },
    ],
  },
};

export const run = async () => {
  try {
    const data = await ddbClient.send(new BatchWriteItemCommand(params));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.BatchWriterItemTVV3]
// For unit tests only.
// module.exports ={run, params};
