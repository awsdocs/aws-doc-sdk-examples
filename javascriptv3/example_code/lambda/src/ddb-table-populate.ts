/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
ddb-table-populate.ts demonstrates how to populate an Amazon DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
ts-node ddb-table-populate.ts
*/

// snippet-start:[lambda.JavaScript.tutorial.PopulateTableV3]
// Load the DynamoDB client
const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb");

//Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({ region: REGION });
//Set the parameters
const myTable = "TABLE_NAME"; //TABLE_NAME

// Add the four spade results
const run = async () => {
  let params = {
    TableName: myTable,
    Item: { slotPosition: { N: "0" }, imageFile: { S: "spad_a.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "1" }, imageFile: { S: "spad_k.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "2" }, imageFile: { S: "spad_q.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "3" }, imageFile: { S: "spad_j.png" } },
  };
  await post(params);

  // Add the four heart results
  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "4" }, imageFile: { S: "hart_a.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "5" }, imageFile: { S: "hart_k.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "6" }, imageFile: { S: "hart_q.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "7" }, imageFile: { S: "hart_j.png" } },
  };
  await post(params);

  // Add the four diamonds results
  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "8" }, imageFile: { S: "diam_a.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "9" }, imageFile: { S: "diam_k.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "10" }, imageFile: { S: "diam_q.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "11" }, imageFile: { S: "diam_j.png" } },
  };
  await post(params);

  // Add the four clubs results
  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "12" }, imageFile: { S: "club_a.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "13" }, imageFile: { S: "club_k.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "14" }, imageFile: { S: "club_q.png" } },
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: { slotPosition: { N: "15" }, imageFile: { S: "club_j.png" } },
  };
  await post(params);
};
run();

const post = async (params) => {
  try {
    const data = await ddb.send(new PutItemCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[lambda.JavaScript.tutorial.PopulateTableV3]

