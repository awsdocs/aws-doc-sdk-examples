/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
deleteTable.js demonstrates how to use the Amazon DynamoDB client to delete a table.

INPUTS:
- TABLE_NAME

Running the code:
node deleteTable.js

*/
// snippet-start:[dynamodb.JavaScript.movies.deleteTableV3]
// Import required AWS SDK clients and commands for Node.js.
import { DeleteTableCommand } from "@aws-sdk/client-dynamodb";
import { ddbClient } from "../libs/ddbClient.js";

// Set the parameters.
export const params = {
  TableName: "TABLE_NAME",
};

export const deleteTable = async () => {
  try {
    const data = await ddbClient.send(new DeleteTableCommand(params));
    console.log("Success, table deleted", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
deleteTable();
// snippet-end:[dynamodb.JavaScript.movies.deleteTableV3]
