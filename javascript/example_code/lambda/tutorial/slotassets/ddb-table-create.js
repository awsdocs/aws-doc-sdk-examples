/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

// Load the DynamoDB client
const { DynamoDBClient, CreateTableCommand } = require('@aws-sdk/client-dynamodb');
// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({region: 'us-west-2'});
// Define the table schema
var tableParams = {
  AttributeDefinitions: [
    {
      AttributeName: 'slotPosition',
      AttributeType: 'N'
    }
  ],
  KeySchema: [
    {
      AttributeName: 'slotPosition',
      KeyType: 'HASH'
    }
  ],
  ProvisionedThroughput: {
    ReadCapacityUnits: 5,
    WriteCapacityUnits: 5
  },
  //TODO: change back to TABLE_NAME
  TableName: 'v3-lambda-tutorial-table',
  StreamSpecification: {
    StreamEnabled: false
  }
};

async function run() {
  try {
    const data = await ddb.send(new CreateTableCommand(tableParams));
    console.log('Success', data);
  } catch(err) {
    console.log('Error', err);
  }
}

run();
