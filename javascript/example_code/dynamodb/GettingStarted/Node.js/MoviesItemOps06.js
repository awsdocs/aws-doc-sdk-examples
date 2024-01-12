// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.CodeExample.MoviesItemOps06]
/*/
var AWS = require("aws-sdk");

AWS.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000",
});

var docClient = new AWS.DynamoDB.DocumentClient();

var table = "Movies";

var year = 2015;
var title = "The Big New Movie";

var params = {
  TableName: table,
  Key: {
    year: year,
    title: title,
  },
  ConditionExpression: "info.rating <= :val",
  ExpressionAttributeValues: {
    ":val": 5.0,
  },
};

console.log("Attempting a conditional delete...");
docClient.delete(params, function (err, data) {
  if (err) {
    console.error(
      "Unable to delete item. Error JSON:",
      JSON.stringify(err, null, 2)
    );
  } else {
    console.log("DeleteItem succeeded:", JSON.stringify(data, null, 2));
  }
});
// snippet-end:[dynamodb.JavaScript.CodeExample.MoviesItemOps06]
