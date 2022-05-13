/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
update-table.js is part of a tutorial demonstrating how to build an Amazon DynamoDB item tracker app.
update-table.js creates and updates an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME

Running the code:
node create-table.js
 */
// snippet-start:[cross-service.JavaScript.ddb-item-tracker.createTable]
// Import required AWS SDK clients and commands for Node.js
import { CreateTableCommand, PutItemCommand } from "@aws-sdk/client-dynamodb";
import { dynamoClient } from "../libs/dynamoClient.js";

const TABLE_NAME = "TABLE_NAME"; // For example, "Work".

export const createTable = async () => {
  try {
    // Set the table parameters.
    const tableParams = {
      AttributeDefinitions: [
        {
          AttributeName: "id",
          AttributeType: "S",
        },
      ],
      KeySchema: [
        {
          AttributeName: "id",
          KeyType: "HASH",
        },
      ],
      ProvisionedThroughput: {
        ReadCapacityUnits: 5,
        WriteCapacityUnits: 5,
      },
      TableName: TABLE_NAME,
    };
    const data = await dynamoClient.send(new CreateTableCommand(tableParams));
    console.log("Success. Table created.");
  } catch (err) {
    console.error("Error", err);
  }
};
createTable();
// snippet-end:[cross-service.JavaScript.ddb-item-tracker.createTable]
