/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

// ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide (scheduled for release later in 2020) topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_batchwriteritem_tv.ts populates the table used for the match query example
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Inputs (replace in code):
- REGION

Running the code:
ts-node ddb_batchwriteritem_tv.ts
*/
// snippet-start:[dynamodb.JavaScript.batch.BatchWriterItemTVV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  BatchWriteItemCommand,
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

// Set the parameters
const params = {
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

const run = async () => {
  try {
    const data = await dbclient.send(new BatchWriteItemCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.BatchWriterItemTVV3]
//for unit tests only
export = {run};
