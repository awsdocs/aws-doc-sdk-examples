//Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

/* ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html
Purpose:
Inputs:
Running the code:

[Outputs | Returns]:
*/

// Call DynamoDB to write the items to the table
const batchWriteTheItems = async(client, command)=> {
    try{
        const data = await client.send(command)
        console.log("Success", data);
    }
    catch(err){
        console.log("Error", err);
    }
};

//Create the DynamoDB Client (& run createTheTable)
const run = async()=> {
    var params = {
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
    }
    const {DynamoDBClient, BatchWriteItemCommand } = require("@aws-sdk/client-dynamodb");
    const region = "REGION"
    const client = await new DynamoDBClient({region: region});
    const command = new BatchWriteItemCommand(params);
    batchWriteTheItems(client, command);
};
run();
exports.batchWriteTheItems = batchWriteTheItems;
