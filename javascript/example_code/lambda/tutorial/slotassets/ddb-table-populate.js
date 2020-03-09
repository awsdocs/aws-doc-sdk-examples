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

var myTable = 'TABLE_NAME';

// Add the four spade results
var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '0'}, 'imageFile' : {S: 'spad_a.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '1'}, 'imageFile' : {S: 'spad_k.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '2'}, 'imageFile' : {S: 'spad_q.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '3'}, 'imageFile' : {S: 'spad_j.png'}
  }
};
post();

// Add the four heart results
var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '4'}, 'imageFile' : {S: 'hart_a.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '5'}, 'imageFile' : {S: 'hart_k.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '6'}, 'imageFile' : {S: 'hart_q.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '7'}, 'imageFile' : {S: 'hart_j.png'}
  }
};
post();

// Add the four diamonds results
var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '8'}, 'imageFile' : {S: 'diam_a.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '9'}, 'imageFile' : {S: 'diam_k.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '10'}, 'imageFile' : {S: 'diam_q.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '11'}, 'imageFile' : {S: 'diam_j.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '12'}, 'imageFile' : {S: 'club_a.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '13'}, 'imageFile' : {S: 'club_k.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '14'}, 'imageFile' : {S: 'club_q.png'}
  }
};
post();

var params = {
  TableName: myTable,
  Item: {'slotPosition' : {N: '15'}, 'imageFile' : {S: 'club_j.png'}
  }
};
post();


function post () {
  ddb.putItem(params, function(err, data) {
    if (err) {
      console.log("Error", err);
    } else {
      console.log("Success", data);
    }
  });
}
