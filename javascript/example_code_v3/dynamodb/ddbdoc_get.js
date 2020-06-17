/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-document-client.html

Purpose:
ddbdoc_get.js demonstrates how to use a DocumentClient to retrieve a set of attributes for an item in an Amazon DynamoDB tab

Inputs:
- REGION (in command line input below)
- TABLE_NAME (in code)
- KEY_NAME (in code)
- VALUE (in code)

Running the code:
node ddbdoc_get.js REGION
*/
// snippet-start:[dynamodb.JavaScript.docClient.get]
async function run() {
    try {
        const {
            DynamoDBClient, GetItemCommand, DocumentClient
        } = require("@aws-sdk/client-dynamodb");
        const region = process.argv[2];
        const params = {
            TableName: 'TABLE_NAME',
            Key: {'KEY_NAME': 'VALUE'}
        };
        const dbclient = new DynamoDBClient.DocumentClient({region: region});
        const data = await dbclient.send(new GetItemCommand(params));
        console.log("Success, item retrieved", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.get]
exports.run = run;
