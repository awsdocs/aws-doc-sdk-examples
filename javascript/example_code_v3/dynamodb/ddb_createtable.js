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
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-examples-using-tables.html
Purpose:
ddb_createtable.js demonstrates how to create an Amazon DynamoDB table.

Inputs:
- ATTRIBUTE_NAME_1 : The name of the partition key
- ATTRIBUTE_NAME_2 : The name of the sort key (optional)
- ATTRIBUTE_TYPE: The type of the attibute (e.g. N [for a number], S [for a string] etc.

Running the code:
node ddb_createtable.js REGION
*/

// snippet-start:[dynamodb.JavaScript.table.createTable]
async function run() {
  try {
    const params = {
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
    const region = process.argv[2];
    const dbclient = new DynamoDBClient({region: region});
    const data = await dbclient.send(new CreateTableCommand(params))
    console.log("Table Created", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.createTable]
//for unit tests only
const params = {
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
exports.params = params;
exports.run = run;
