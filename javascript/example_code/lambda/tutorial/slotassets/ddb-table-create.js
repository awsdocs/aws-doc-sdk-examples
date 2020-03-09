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

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Load credentials and set region from JSON file
AWS.config.loadFromPath('./config.json');

// Create DynamoDB service object
var ddb = new AWS.DynamoDB({apiVersion: '2012-08-10'});

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
  TableName: 'TABLE_NAME',
  StreamSpecification: {
    StreamEnabled: false
  }
};

ddb.createTable(tableParams, function(err, data) {
  if (err) {
    console.log("Error", err);
  } else {
    console.log("Success", data);

  }
});
