/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
update-table.ts is part of a tutorial demonstrating how to build and deploy an app to submit
data to an Amazon DynamoDB table.
To run the full tutorial, see https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html.
update-table.ts demonstrates how to update a DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
node create-table.ts
 */
// snippet-start:[s3.JavaScript.cross-service.updateTableV3]
// Import required AWS SDK clients and commands for Node.js
const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  TableName: "TABLE_NAME",
  Item: {
    Id: { N: "1" },
    Title: { S: "aTitle" },
    Name: { S: "aName" },
    Body: { S: "aBody" },
  },
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

const run = async () => {
  try {
    const data = await dbclient.send(new PutItemCommand(params));
    console.log("success");
    console.log(data);
  } catch (err) {
    console.error(err);
  }
};
run();
// snippet-end:[s3.JavaScript.cross-service.updateTableV3]
//for unit tests only
exports.run = run;
