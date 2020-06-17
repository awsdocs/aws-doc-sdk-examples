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
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-examples-using-tables.html

Purpose:
ddb_deletetable.js demonstrates how to delete an Amazon DynamoDB table.

Inputs:
-TABLE_NAME

Running the code:
node.js ddb_deletetable.js

*/
// snippet-start:[dynamodb.JavaScript.table.deleteTable]
const run = async()=> {
  try {
    const params = {
      TableName: "CUSTOMER_LIST",
      Key: {
        'CUSTOMER_NAME': {N: 'Richard Roe'}
      }
    };
    const {
      DynamoDBClient, DeleteTableCommand
    } = require("@aws-sdk/client-dynamodb");
    const region = "REGION";
    const dbclient = await new DynamoDBClient({region: region});
    const data = await dbclient.send(new DeleteTableCommand(params))
    console.log("Table deleted", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.deleteTable]
exports.run = run;
const params = {
  TableName: "CUSTOMER_LIST",
  Key:{
    'CUSTOMER_NAME': {N:'Richard Roe'}
  }
};
exports.params = params;
