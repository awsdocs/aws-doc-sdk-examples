/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples-using-tables.html.

Purpose:
ddb_createtable.js demonstrates how to create an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)
- ATTRIBUTE_NAME_1 : (in code; the name of the partition key)
- ATTRIBUTE_NAME_2 : (in code; the name of the sort key (optional))
- ATTRIBUTE_TYPE (in code; the type of the attribute (e.g., N [for a number], S [for a string] etc.)

Running the code:
node ddb_createtable.js REGION TABLE_NAME
*/

// snippet-start:[dynamodb.JavaScript.v3.table.createTable]
// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, CreateTableCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS Region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {
  AttributeDefinitions: [
    {
      AttributeName: 'Season', //ATTRIBUTE_NAME_1
      AttributeType: 'N' //ATTRIBUTE_TYPE
    },
    {
      AttributeName: 'Episode', //ATTRIBUTE_NAME_2
      AttributeType: 'N' //ATTRIBUTE_TYPE
    }
  ],
  KeySchema: [
    {
      AttributeName: 'Season', //ATTRIBUTE_NAME_1
      KeyType: 'HASH'
    },
    {
      AttributeName: 'Episode', //ATTRIBUTE_NAME_2
      KeyType: 'RANGE'
    }
  ],
  ProvisionedThroughput: {
    ReadCapacityUnits: 1,
    WriteCapacityUnits: 1
  },
  TableName: process.argv[3], //TABLE
  StreamSpecification: {
    StreamEnabled: false
  }
};

async function run() {
  try {
    const data = await dbclient.send(new CreateTableCommand(params))
    console.log("Table Created", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.table.createTable]
//for unit tests only
exports.run = run; //for unit tests only
