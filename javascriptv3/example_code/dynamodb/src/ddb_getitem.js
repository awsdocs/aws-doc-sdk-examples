/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_getitem.js demonstrates how to retrieve the attributes of an item from an Amazon DynamoDB table.

INPUTS:
- TABLE_NAME
- KEY_NAME: the primary key of the table, e.g., 'CUSTOMER_ID'
- KEY_NAME_VALUE: the value of the primary key row containing the attribute value
- ATTRIBUTE_NAME: the name of the attribute column containing the attribute value

Running the code:
node ddb_getitem.js
*/
// snippet-start:[dynamodb.JavaScript.item.getItemV3]
// Import required AWS SDK clients and commands for Node.js
import { GetItemCommand } from "@aws-sdk/client-dynamodb";
import { ddbClient } from "./libs/ddbClient.js";

// Set the parameters
export const params = {
  TableName: "TABLE_NAME", //TABLE_NAME
  Key: {
    KEY_NAME: { N: "KEY_VALUE" },
  },
  ProjectionExpression: "ATTRIBUTE_NAME",
};

export const run = async () => {
  const data = await ddbClient.send(new GetItemCommand(params));
  console.log("Success", data.Item);
  return data;
  
};
run();
// snippet-end:[dynamodb.JavaScript.item.getItemV3]
// For unit tests only.
// module.exports ={run, params};
