/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide (scheduled for release later in 2020) topic at
 https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_createtable_tv.test.js creates a table for creating a table for the match query example
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.


Running the code:
node ddb_createtable_tv.test.js
*/
// snippet-start:[dynamodb.JavaScript.batch.CreateTableTVV3]
// Import required AWS SDK clients and commands for Node.js
import {
  CreateTableCommand
} from"@aws-sdk/client-dynamodb";
import { ddbClient } from "../libs/ddbClient.js";

// Set the parameters
export const params = {
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

export const run = async () => {
  try {
    const data = await ddbClient.send(new CreateTableCommand(params));
    console.log("Table Created", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.CreateTableTVV3]
// For unit tests only.
// module.exports ={run, params};
