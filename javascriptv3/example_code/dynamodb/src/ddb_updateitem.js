/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_putitem.js demonstrates how to do a partial update of an item in an Amazon DynamoDB table.

INPUTS:
- TABLE_NAME
- ATTRIBUTE_TYPE


Running the code:
node ddb_updateitem.js
*/
// snippet-start:[dynamodb.JavaScript.item.updateItemV3]

// Import required AWS SDK clients and commands for Node.js
import { UpdateItemCommand } from "@aws-sdk/client-dynamodb";
import { ddbClient } from "./libs/ddbClient.js";

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
        primaryKey: {"ATTRIBUTE_TYPE":"KEY_VALUE"}, // For example, 'Season': {N:2}.
        sortKey: {"ATTRIBUTE_TYPE":"KEY_VALUE"} // For example,  'Episode': {S: "The return"}; (only required if table has sort key).
    },
    // Define expressions for the new or updated attributes
    UpdateExpression: "set NEW_ATTRIBUTE_NAME_1 = :t, NEW_ATTRIBUTE_NAME_2 = :s", // For example, "'set Title = :t, Subtitle = :s'"
    ExpressionAttributeValues: {
        ":t": "NEW_ATTRIBUTE_VALUE_1", // For example ':t' : 'NEW_TITLE'
        ":s": "NEW_ATTRIBUTE_VALUE_2", // For example ':s' : 'NEW_SUBTITLE'
    },
    ReturnValues: "ALL_NEW"
};
export const run = async () => {
    try {
        const data = await ddbClient.send(new UpdateItemCommand(params));
        console.log(data);
        return data;
    } catch (err) {
        console.error(err);
    }
};
run();
// snippet-end:[dynamodb.JavaScript.item.updateItemV3]

