/* ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html
Purpose:
ddb_createtable_tv.js creates a table for creating a table for the match query example https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html .

Inputs:
- REGION

Running the code:
node ddb_createtable_tv.js REGION
*/
// snippet-start:[dynamodb.JavaScript.v3.batch.CreateTableTV]
// Import required AWS-SDK clients and commands for Node.js
const {DynamoDBClient, CreateTableCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
var params = {
    AttributeDefinitions: [
        {
            AttributeName: 'Season',
            AttributeType: 'N'
        },
        {
            AttributeName: 'Episode',
            AttributeType: 'N'
        }
    ],
    KeySchema: [
        {
            AttributeName: 'Season',
            KeyType: 'HASH'
        },
        {
            AttributeName: 'Episode',
            KeyType: 'RANGE'
        }
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1
    },
    TableName: 'EPISODES_TABLE',
    StreamSpecification: {
        StreamEnabled: false
    }
};

async function run() {
    try {
        const data = await dbclient.send(new CreateTableCommand(params));
        console.log("Table Created", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.batch.CreateTableTV]
exports.run = run;
