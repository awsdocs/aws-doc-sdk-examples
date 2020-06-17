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
ddb_describetable.js demonstrates how to retrieve information about an Amazon DynamoDB table.

Inputs:

Running the code:
node ddb_describetable.js
*/
// snippet-start:[dynamodb.JavaScript.table.describeTable]
// Call DynamoDB to create the table
async function run() {
  try {
    const {DynamoDBClient, DescribeTableCommand} = require("@aws-sdk/client-dynamodb");
    const region= "REGION"
    const params = {TableName: "TABLE_NAME"};
    const dbclient = await new DynamoDBClient({region: region});
    const data = await dbclient.send(new DescribeTableCommand(params));
    console.log("Success", data.Table.KeySchema);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.describeTable]
exports.run = run;
const params = {TableName: "TABLE_NAME"};
exports.params = params;
