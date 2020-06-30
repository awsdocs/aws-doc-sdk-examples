/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

 Purpose:
    ddb-table-populate.test.js demonstrates how to populate an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)

Running the code:
    node ddb-table-populate.js REGION TABLE_NAME
*/

// snippet-start:[lambda.JavaScript.v3.PopulateTable]
// Load the DynamoDB client
const { DynamoDBClient, PutItemCommand } = require('@aws-sdk/client-dynamodb');
// Instantiate a DynamoDB client
const region = process.argv[2]; //REGION
const ddb = new DynamoDBClient(region);

const myTable = process.argv[3]; //TABLE_NAME

// Add the four spade results
async function run() {
  let params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '0'}, 'imageFile' : {S: 'spad_a.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '1'}, 'imageFile' : {S: 'spad_k.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '2'}, 'imageFile' : {S: 'spad_q.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '3'}, 'imageFile' : {S: 'spad_j.png'}
    }
  };
  await post(params);

  // Add the four heart results
  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '4'}, 'imageFile' : {S: 'hart_a.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '5'}, 'imageFile' : {S: 'hart_k.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '6'}, 'imageFile' : {S: 'hart_q.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '7'}, 'imageFile' : {S: 'hart_j.png'}
    }
  };
  await post(params);

  // Add the four diamonds results
  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '8'}, 'imageFile' : {S: 'diam_a.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '9'}, 'imageFile' : {S: 'diam_k.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '10'}, 'imageFile' : {S: 'diam_q.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '11'}, 'imageFile' : {S: 'diam_j.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '12'}, 'imageFile' : {S: 'club_a.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '13'}, 'imageFile' : {S: 'club_k.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '14'}, 'imageFile' : {S: 'club_q.png'}
    }
  };
  await post(params);

  params = {
    TableName: myTable,
    Item: {'slotPosition' : {N: '15'}, 'imageFile' : {S: 'club_j.png'}
    }
  };
  await post(params);
}

run();

async function post (params) {
  try {
    const data = await ddb.send(new PutItemCommand(params));
    console.log("Success", data);
  } catch(err) {
    console.log("Error", err);
  }
}

// snippet-end:[lambda.JavaScript.v3.PopulateTable]
