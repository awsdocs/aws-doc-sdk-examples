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

/* ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write-batch.html

Purpose:
ddb_batchwriteitem.js demonstrates how to put or delete items into an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)

Running the code:
node ddb_batchwriteitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.batch.WriteItem]
async function run() {
  try {
    const params = {
      RequestItems: {
        "TABLE_NAME": [
          {
            PutRequest: {
              Item: {
                "KEY": {"N": "KEY_VALUE"},
                "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
                "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
              }
            }
          },
          {
            PutRequest: {
              Item: {
                "KEY": {"N": "KEY_VALUE"},
                "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
                "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
              }
            }
          }
        ]
      }
    };
    const {
      DynamoDBClient, BatchWriteItemCommand
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const dbclient = await new DynamoDBClient(region);
    const data = await dbclient.send(new BatchWriteItemCommand(params));
    console.log("Success, items inserted", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.WriteItem]
//for unit tests only
exports.run = run;
const params = {
  RequestItems: {
    "TABLE_NAME": [
      {
        PutRequest: {
          Item: {
            "KEY": {"N": "KEY_VALUE"},
            "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
            "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
          }
        }
      },
      {
        PutRequest: {
          Item: {
            "KEY": {"N": "KEY_VALUE"},
            "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
            "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
          }
        }
      }
    ]
  }
};
exports.params = params;
