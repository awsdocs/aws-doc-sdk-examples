/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
putItem.js demonstrates how to use the Amazon DynamoDB document client to put an item in a table.

Inputs (replace in code):
- TABLE_NAME
- primaryKey - The name of the primary key. For example, "title".
- VALUE_1: Value for the primary key. (The format for the datatype must match the schema. For example, if the primaryKey is a number, VALUE_1 has no inverted commas.)
- sortKey - The name of the sort key. Only required if the table has sort key. For example, "year".
- VALUE_2: Value for the primary key. (The format for the datatype must match the schema.)

Running the code:
node putItem.js
*/
// snippet-start:[dynamodb.JavaScript.movies.putItemV3]
import { PutCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

export const putItem = async () => {
  // Set the parameters.
  export const params = {
    TableName: "TABLE_NAME",
    Item: {
      primaryKey: "VALUE_1",
      sortKey: "VALUE_2",
    },
  };
  try {
    const data = await ddbDocClient.send(new PutCommand(params));
    console.log("Success - item added or updated", data);
  } catch (err) {
    console.log("Error", err.stack);
  }
};
putItem();
// snippet-end:[dynamodb.JavaScript.movies.putItemV3]
