/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
ddb-table-create.ts demonstrates how to create a Amazon DynamoDB database table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
ts-node ddb-table-create.ts
*/
// snippet-start:[lambda.JavaScript.tutorial.CreateTableV3]
// Load the required clients and packages
const {
  DynamoDBClient,
  CreateTableCommand
} = require("@aws-sdk/client-dynamodb");

//Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({ region: REGION });

// Define the table schema
const tableParams = {
  AttributeDefinitions: [
    {
      AttributeName: "slotPosition",
      AttributeType: "N",
    },
  ],
  KeySchema: [
    {
      AttributeName: "slotPosition",
      KeyType: "HASH",
    },
  ],
  ProvisionedThroughput: {
    ReadCapacityUnits: 5,
    WriteCapacityUnits: 5,
  },

  TableName: "TABLE_NAME", //TABLE_NAME
  StreamSpecification: {
    StreamEnabled: false,
  },
};

const run = async () => {
  try {
    const data = await ddb.send(new CreateTableCommand(tableParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};

run();
// snippet-end:[lambda.JavaScript.tutorial.CreateTableV3]

