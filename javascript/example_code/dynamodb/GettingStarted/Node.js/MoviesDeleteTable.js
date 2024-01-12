// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.CodeExample.MoviesDeleteTable]
var AWS = require("aws-sdk");

AWS.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000",
});

var dynamodb = new AWS.DynamoDB();

var params = {
  TableName: "Movies",
};

dynamodb.deleteTable(params, function (err, data) {
  if (err) {
    console.error(
      "Unable to delete table. Error JSON:",
      JSON.stringify(err, null, 2)
    );
  } else {
    console.log(
      "Deleted table. Table description JSON:",
      JSON.stringify(data, null, 2)
    );
  }
});

// snippet-end:[dynamodb.JavaScript.CodeExample.MoviesDeleteTable]
