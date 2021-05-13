/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide (scheduled for release later in 2020) topic at
 https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_createtable_tv.js creates a table for creating a table for the match query example
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Inputs (replace in code):
- REGION

Running the code:
node ddb_createtable_tv.js
*/
// snippet-start:[dynamodb.JavaScript.batch.CreateTableTVV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  CreateTableCommand
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  AttributeDefinitions: [
    {
      AttributeName: "Season",
      AttributeType: "N",
    },
    {
      AttributeName: "Episode",
      AttributeType: "N",
    }
  ],
  KeySchema: [
    {
      AttributeName: "Season",
      KeyType: "HASH",
    },
    {
      AttributeName: "Episode",
      KeyType: "RANGE",
    }
  ],
  ProvisionedThroughput: {
    ReadCapacityUnits: 1,
    WriteCapacityUnits: 1,
  },
  TableName: "EPISODES_TABLE",
  StreamSpecification: {
    StreamEnabled: false,
  },
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient({ region: REGION });

const run = async () => {
  try {
    const data = await dbclient.send(new CreateTableCommand(params));
    console.log("Table Created", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.CreateTableTVV3]
//for unit tests only
export = {run};
