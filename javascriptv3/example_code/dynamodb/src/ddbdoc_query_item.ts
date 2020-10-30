/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-document-client.html.

Purpose:
ddbdoc_update_query.ts demonstrates how to use DynamoDB utilities to retrieve items from an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- REGION

Running the code:
ts-node ddbdoc_update_query.ts
*/
// snippet-start:[dynamodb.JavaScript.docClient.queryV3]

// Import required AWS SDK clients and commands for Node.js
const { DynamoDB } = require("@aws-sdk/client-dynamodb");
const { marshall, unmarshall } = require("@aws-sdk/util-dynamodb");

// Set the parameters
const params = {
  TableName: "TABLE_NAME",
  /*
  Convert the JavaScript object defining the objects to the required DynamoDB format.The format of values
  specifies the datatype. The following list demonstrates different datatype formatting requirements:
  HashKey: "hashKey",
  NumAttribute: 1,
  BoolAttribute: true,
  ListAttribute: [1, "two", false],
  MapAttribute: { foo: "bar" },
  NullAttribute: null
   */
  ExpressionAttributeValues: marshall({
    ":s": 2,
    ":e": 9,
    ":topic": "The Return",
  }),
  // Specifies the values that define the range of the retrieved items. In this case, items in Season 2 before episode 9.
  KeyConditionExpression: "Season = :s and Episode > :e",
  // Filter that returns only episodes that meet previous criteria and have the subtitle 'The Return'
  FilterExpression: "contains (Subtitle, :topic)",
};

// Create DynamoDB document client
const client = new DynamoDB({ region: "us-east-1" });

const run = async () => {
  try {
    const data = await client.query(params);
    console.log("Success - query");
    console.log(data.Items);
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[dynamodb.JavaScript.docClient.queryV3]
