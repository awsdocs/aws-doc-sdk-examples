/* ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html
Purpose:
Inputs:
Running the code:

[Outputs | Returns]:
*/
// Call DynamoDB to create the table
const createTheTable = async  (client, command)=> {
    try{
        const data = await client.send(command)
        console.log("Table Created", data);
    }
    catch(err){
        console.log("Error", err);
    }
};

//Create the DynamoDB Client (& run createTheTable)
const run = async()=> {
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
    const {
        DynamoDBClient, CreateTableCommand
    } = require("@aws-sdk/client-dynamodb");
    const client = await new DynamoDBClient({region: "eu-west-1"});
    const command = new CreateTableCommand(params);
    createTheTable(client, command);
};
run();
exports.createTheTable = createTheTable;
