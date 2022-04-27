/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-document-client.html.

Purpose:
ddbdoc_update_item.js demonstrates how to use the Amazon DynamoDB document client to create or update an item in an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- primaryKey - Name of the primary key. For example, "id".
- VALUE_1
- sortKey - Name of the sor key. For example, "firstName".
- VALUE_2
- NEW_ATTRIBUTE_VALUE_1
- NEW_ATTRIBUTE_VALUE_2

Running the code:
node ddbdoc_update_item.js
*/
// snippet-start:[dynamodb.JavaScript.docClient.updateV3]
import { UpdateCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "./libs/ddbDocClient";

// Set the parameters
export const params = {
  TableName: "TABLE_NAME",
  /*
  Convert the attribute JavaScript object you are updating to the required
  Amazon  DynamoDB record. The format of values specifies the datatype. The
  following list demonstrates different datatype formatting requirements:
  String: "String",
  NumAttribute: 1,
  BoolAttribute: true,
  ListAttribute: [1, "two", false],
  MapAttribute: { foo: "bar" },
  NullAttribute: null
   */
  Key: {
    primaryKey: "VALUE_1", // For example, 'Season': 2.
    sortKey: "VALUE_2", // For example,  'Episode': 1; (only required if table has sort key).
  },
  // Define expressions for the new or updated attributes
  UpdateExpression: "set ATTRIBUTE_NAME_1 = :t, ATTRIBUTE_NAME_2 = :s", // For example, "'set Title = :t, Subtitle = :s'"
  ExpressionAttributeValues: {
    ":t": "NEW_ATTRIBUTE_VALUE_1", // For example ':t' : 'NEW_TITLE'
    ":s": "NEW_ATTRIBUTE_VALUE_2", // For example ':s' : 'NEW_SUBTITLE'
  },
  ReturnValues: "ALL_NEW"
};

export const run = async () => {
  try {
    const data = await ddbDocClient.send(new UpdateCommand(params));
    console.log("Success - item added or updated", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.updateV3]

