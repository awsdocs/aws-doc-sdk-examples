/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
/* ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write.html

Purpose:
ddb_getitem.js demonstrates how to retrieve the attributes of an item from an Amazon DynamoDB table.

Inputs:
- REGION
- TABLE: the name of the table;
- KEY_NAME: the primary key of the table e.g.'CUSTOMER_ID'
- KEY_NAME_VALUE: the value
- ATTRIBUTE_NAME:
- ATTRIBUTE_NAME_VALUE:

Running the code:
node ddb_getitem.js

*/
// snippet-start:[dynamodb.JavaScript.item.getItem]
const run = async()=> {
  const params = {
    TableName: 'CUSTOMER_LIST',
    Key: {
      'CUSTOMER_ID' : {N: '001'}
    },
    ProjectionExpression: 'CUSTOMER_NAME'
  };
  const {
    DynamoDBClient, GetItemCommand
  } = require("@aws-sdk/client-dynamodb");
  const {DocumentClient} = require("@aws-sdk/client-docdb")
  const region = "REGION";
  const dbclient = new DynamoDBClient({region: region});
  const data = await dbclient.send(new GetItemCommand(params));
  console.log("Success", data.Item);
};
run();
// snippet-end:[dynamodb.JavaScript.item.getItem]
const params = {
  TableName: 'CUSTOMER_LIST',
  Key: {
    'CUSTOMER_ID' : {N: '001'}
  },
  ProjectionExpression: 'CUSTOMER_NAME'
};
exports.run = run;
exports.params = params;
