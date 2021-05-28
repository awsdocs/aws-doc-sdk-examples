/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-dynamodb-utilities.html.

Purpose:
ddbdoc_put_item.ts demonstrates how to use Amazon DynamoDB utilities to create or replace an item in an Amazon DynamoDB table.

Inputs (replace in code):
- TABLE_NAME
- REGION
- primaryKey
- sortKey (only required if table has sort key)
- VALUE_1: Value for the primary key (The format for the datatype must match the schema. For example, if the primaryKey is a number, VALUE_1 has no inverted commas.)
- VALUE_2: Value for the primary key (The format for the datatype must match the schema.)
- NEW_ATTRIBUTE_1
- NEW_ATTRIBUTE_1_VALUE

Running the code:
ts-node ddbdoc_put_item.ts
*/
// snippet-start:[dynamodb.JavaScript.docClient.putV3]

// Import the required AWS SDK clients and command for Node.js
const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb");
const { marshall } = require("@aws-sdk/util-dynamodb");

// Set the parameters
const TableName = "TABLE_NAME";
/*
Convert the key JavaScript object you are creating or replacing
to the required Amazon DynamoDB record. The format of values specifies
the datatype. The following list demonstrates different datatype
formatting requirements:
String: "String",
NumAttribute: 1,
BoolAttribute: true,
ListAttribute: [1, "two", false],
MapAttribute: { foo: "bar" },
NullAttribute: null
 */
const input = {
    primaryKey: VALUE_1, // For example, "Season: 2"
    sortKey: VALUE_2 // For example,  "Episode: 2" (only required if table has sort key)
    NEW_ATTRIBUTE_1: NEW_ATTRIBUTE_1_VALUE //For example "'Title': 'The Beginning'"
};
// Marshall util converts then JavaScript object to DynamoDB format
const Item = marshall(input);

// Create DynamoDB client
const client = new DynamoDBClient({ region:"REGION" });

const run = async () => {
    try {
        const data = await client.send(new PutItemCommand(TableName, Item));
        console.log('Success - put')}
    catch(err){
        console.log('Error', err)}
}
run();
// snippet-end:[dynamodb.JavaScript.docClient.putV3]
