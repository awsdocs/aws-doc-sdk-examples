/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-dynamodb-utilities.html.

Purpose:
ddbdoc_put_item.js demonstrates how to use the Amazon DynamoDB document client to create or replace an item in an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- primaryKey - The name of the primary key. For example, "id".
- VALUE_1: Value for the primary key (The format for the datatype must match the schema. For example, if the primaryKey is a number, VALUE_1 has no inverted commas.)
- sortKey - The name of the sort key. Only required if table has sort key. For example, "firstName".
- VALUE_2: Value for the sort key (The format for the datatype must match the schema.)
- NEW_ATTRIBUTE_1
- NEW_ATTRIBUTE_1_VALUE

Running the code:
node ddbdoc_put_item.js
*/
// snippet-start:[dynamodb.JavaScript.docClient.putV3]

import { PutCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "./libs/ddbDocClient";

// Set the parameters.
export const params = {
  TableName: "TABLE_NAME",
  /*
    Convert the key JavaScript object you are adding to the
    required Amazon DynamoDB record. The format of values specifies
    the datatype. The following list demonstrates different
    datatype formatting requirements:
    String: "String",
    NumAttribute: 1,
    BoolAttribute: true,
    ListAttribute: [1, "two", false],
    MapAttribute: { foo: "bar" },
    NullAttribute: null
     */
  Item: {
    primaryKey: "VALUE_1", // For example, 'Season': 2
    sortKey: "VALUE_2", // For example,  'Episode': 2 (only required if table has sort key)
    NEW_ATTRIBUTE_1: "NEW_ATTRIBUTE_1_VALUE", //For example 'Title': 'The Beginning'
  },
};

export const run = async () => {
  try {
    const data = await ddbDocClient.send(new PutCommand(params));
    console.log("Success - item added or updated", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.putV3]
// For unit tests only.
// module.exports = { run, params };
