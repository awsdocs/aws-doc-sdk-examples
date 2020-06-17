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
ddbdoc_query.js demonstrates how to use a DocumentClient to retrieve items from an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)
- TABLE (in code)
- HASH_KEY (in code)
- ATTRIBUTE_# (in code)
- STRING_VALUE (in code)


Running the code:
node ddbdoc_query.js REGION

*/
// snippet-start:[dynamodb.JavaScript.docClient.query]
async function run() {
    try {
        const params = {
            KeyConditionExpression: 'Season = :s and Episode > :e',
            FilterExpression: 'contains (Subtitle, :topic)',
            ExpressionAttributeValues: {
                ':s' : {N: '1'},
                ':e' : {N: '2'},
                ':topic' : {S: 'SubTitle'}
            },
            ProjectionExpression: 'Episode, Title, Subtitle',
            TableName: 'EPISODES_TABLE'
        };
        const {
            DynamoDBClient, QueryCommand, DocumentClient
        } = require("@aws-sdk/client-dynamodb");
        const region = process.argv[2];
        const dbclient = new DynamoDBClient.DocumentClient({region: region});
        const data = await dbclient.send(new QueryCommand(params));
        data.Items.forEach(function(element, index, array) {
            console.log(element.Title.S + " (" + element.Subtitle.S + ")");
        });
         }
    catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.query]
exports.run = run;
