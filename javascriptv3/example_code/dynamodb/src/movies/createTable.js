/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createTable.js demonstrates how to use the Amazon DynamoDB client to create a table.

Inputs (replace in code):
- TABLE_NAME
- VALUE_1: Value for the primary key. (The format for the datatype must match the schema. For example,
// if the primaryKey is a number, VALUE_1 has no inverted commas.)
- VALUE_2: Value for the primary key. (The format for the datatype must match the schema.)

Running the code:
node createTable.js
*/
// snippet-start:[dynamodb.JavaScript.movies.createTableV3]
import { CreateTableCommand } from "@aws-sdk/client-dynamodb";
import { ddbClient } from "../libs/ddbClient.js";

const tableName = process.argv[2];
export const createTable = async () => {
  // Set the parameters.
  const params = {
    AttributeDefinitions: [
      {
        AttributeName: "VALUE_1",
        AttributeType: "S",
      },
      {
        AttributeName: "VALUE_1",
        AttributeType: "N",
      },
    ],
    KeySchema: [
      {
        AttributeName: "VALUE_1",
        KeyType: "HASH",
      },
      {
        AttributeName: "VALUE_2",
        KeyType: "RANGE",
      },
    ],
    TableName: "TABLE_NAME",
    ProvisionedThroughput: {
      ReadCapacityUnits: 5,
      WriteCapacityUnits: 5,
    },
  };
  try {
    const data = await ddbClient.send(new CreateTableCommand(params));
    console.log(
      "Table created. Table name is ",
      data.TableDescription.TableName
    );
  } catch (err) {
    console.log("Error", err);
  }
};
createTable();
// snippet-end:[dynamodb.JavaScript.movies.createTableV3]
