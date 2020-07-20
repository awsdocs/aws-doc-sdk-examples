/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
ddb-table-create.js demonstrates how to create a Amazon DynamoDB database table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
node ddb-table-create.test.js
*/
// snippet-start:[lambda.JavaScript.tutorial.CreateTableV3]

// Load the required clients and packages
const { DynamoDBClient, CreateTableCommand } = require('@aws-sdk/client-dynamodb');

//Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate a DynamoDB client
const ddb = new DynamoDBClient(REGION);

// Define the table schema
var tableParams = {
  AttributeDefinitions: [
    {
      AttributeName: 'slotPosition',
      AttributeType: 'N'
    }
  ],
  KeySchema: [
    {
      AttributeName: 'slotPosition',
      KeyType: 'HASH'
    }
  ],
  ProvisionedThroughput: {
    ReadCapacityUnits: 5,
    WriteCapacityUnits: 5
  },

  TableName: "TABLE_NAME", //TABLE_NAME
  StreamSpecification: {
    StreamEnabled: false
  }
};

const run = async () => {
  try {
    const data = await ddb.send(new CreateTableCommand(tableParams));
    console.log('Success', data);
  } catch(err) {
    console.log('Error', err);
  }
};

run();
// snippet-end:[lambda.JavaScript.tutorial.CreateTableV3]
exports.run = run; //for unit tests only
