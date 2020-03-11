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
const { DynamoDBClient, PutItemCommand } = require('@aws-sdk/client-dynamodb');
// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({region: 'us-west-2'});

const myTable = 'TABLE_NAME';

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
