/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-document-client.html.

Purpose:
ddbdoc_update_item.ts demonstrates how to use the Amazon DynamoDB document client to create or update an item in an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- REGION
- VALUE_1
- VALUE_2
- NEW_ATTRIBUTE_VALUE_1
- NEW_ATTRIBUTE_VALUE_2

Running the code:
ts-node ddbdoc_update_item.ts
*/
// snippet-start:[dynamodb.JavaScript.docClient.updateV3]
import {UpdateCommand} from "@aws-sdk/lib-dynamodb";

const { DynamoDBDocumentClient, PutCommand } = require("@aws-sdk/lib-dynamodb");
const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");

const REGION = "eu-west-1";

const marshallOptions = {
  // Whether to automatically convert empty strings, blobs, and sets to `null`.
  convertEmptyValues: false, // false, by default.
  // Whether to remove undefined values while marshalling.
  removeUndefinedValues: false, // false, by default.
  // Whether to convert typeof object to map attribute.
  convertClassInstanceToMap: false, // false, by default.
};

const unmarshallOptions = {
  // Whether to return numbers as a string instead of converting them to native JavaScript numbers.
  wrapNumbers: false, // false, by default.
};

const translateConfig = { marshallOptions, unmarshallOptions };

// Create the clients.
const client = new DynamoDBClient({ region: REGION });
const ddbDocClient = DynamoDBDocumentClient.from(client, translateConfig); // client is DynamoDB client

// Set the parameters
const params = {
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
  Key:{
    primaryKey: VALUE_1, // For example, 'Season': 2.
    sortKey: VALUE_2, // For example,  'Episode': 1; (only required if table has sort key).
  },
  // Define expressions for the new or updated attributes
  UpdateExpression: "set ATTRIBUTE_NAME_1 = :t, ATTRIBUTE_NAME_2 = :s", // For example, "'set Title = :t, Subtitle = :s'"
  ExpressionAttributeValues: {
    ":t": NEW_ATTRIBUTE_VALUE_1, // For example ':t' : 'NEW_TITLE'
    ":s": NEW_ATTRIBUTE_VALUE_2, // For example ':s' : 'NEW_SUBTITLE'
  },
};

const run = async () => {
  try {
    const data = await ddbDocClient.send(new UpdateCommand(params));
    console.log("Success - item added or updated", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.updateV3]
