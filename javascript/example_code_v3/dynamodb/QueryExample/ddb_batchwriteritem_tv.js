//Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

/* ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html
Purpose:
ddb_batchwriteritem_tv.js populates the table used for the match query example https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html .

Inputs:
- REGION

Running the code:
node ddb_batchwriteritem_tv.js REGION
*/
// snippet-start:[dynamodb.JavaScript.v3.batch.BatchWriterItemTV]
// Import required AWS-SDK clients and commands for Node.js
const {DynamoDBClient, BatchWriteItemCommand } = require("@aws-sdk/client-dynamodb");
// Set the AWS region
const region = process.argv[2];
// Create DynamoDB service object
const client = new DynamoDBClient(region);
// Set the parameters
const params = {
    RequestItems: {
        "EPISODES_TABLE": [
            {
                PutRequest: {
                    Item: {
                        'Season': {N: '1'},
                        'Episode': {N: '1'},
                        'Subtitle': {S: 'SubTitle1'},
                        'Title': {S: 'Title1'}
                    }
                }
            },
            {
                PutRequest: {
                    Item: {
                        'Season': {N: '1'},
                        'Episode': {N: '2'},
                        'Subtitle': {S: 'SubTitle2'},
                        'Title': {S: 'Title2'}
                    }
                }
            },
            {
                PutRequest: {
                    Item: {
                        'Season': {N: '1'},
                        'Episode': {N: '3'},
                        'Subtitle': {S: 'SubTitle3'},
                        'Title': {S: 'Title3'}
                    }
                }
            },
            {
                PutRequest: {
                    Item: {
                        'Season': {N: '1'},
                        'Episode': {N: '4'},
                        'Subtitle': {S: 'SubTitle4'},
                        'Title': {S: 'Title4'}
                    }
                }
            }

        ]
    }
};

async function run(){
    try {
        const data = await dbclient.send(new BatchWriteItemCommand(params));
        console.log("Success", data);
    }
    catch(err){
        console.log("Error", err);
    }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.batch.BatchWriterItemTV]
//for unit tests only
exports.run = run;
