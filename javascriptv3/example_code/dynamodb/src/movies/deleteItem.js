/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
deleteItem.js demonstrates how to use the Amazon DynamoDB document client to delete an item from an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- "PRIMARY_KEY" - The name of the primary key. For example, "title".
- VALUE_1: Value for the primary key. (The format for the datatype must match the schema. For example,
// if the primaryKey is a number, VALUE_1 has no inverted commas.)
- "SORT_KEY" - The name of the sort key. Only required if the table has sort key. For example, "year".
- VALUE_2: Value for the primary key. (The format for the datatype must match the schema.)

Running the code:
node deleteItem.js
*/
// snippet-start:[dynamodb.JavaScript.movies.deleteV3]

import { DeleteCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

// Set the parameters.
export const params = {
  TableName: "TABLE_NAME",
  Key: {
    PRIMARY_KEY: "VALUE_1", //e.g. title: "Rush"
    SORT_KEY: "VALUE_2", // e.g. year: "2013"
  },
};

export const deleteItem = async () => {
  try {
    await ddbDocClient.send(new DeleteCommand(params));
    console.log("Success - item deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
deleteItem();
// snippet-end:[dynamodb.JavaScript.movies.deleteV3]
