/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples-using-tables.html.

Purpose:
ddb_listtables.js demonstrates how to retrieve a list of Amazon DynamoDB table names.

Inputs (replace in code):
- REGION

Running the code:
node ddb_listtables.js
*/
// snippet-start:[dynamodb.JavaScript.table.listTablesV3]

// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, ListTablesCommand} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

const run = async () => {
  try {
    const data = await dbclient.send(new ListTablesCommand({}));
    console.log(data.TableNames.join("\n"));
  } catch (err) {
    console.error(err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.listTablesV3]
exports.run = run; //for unit tests only

