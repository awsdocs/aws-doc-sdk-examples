// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.CodeExample.MoviesScan]
var AWS = require("aws-sdk");

AWS.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000",
});

var docClient = new AWS.DynamoDB.DocumentClient();

var params = {
  TableName: "Movies",
  ProjectionExpression: "#yr, title, info.rating",
  FilterExpression: "#yr between :start_yr and :end_yr",
  ExpressionAttributeNames: {
    "#yr": "year",
  },
  ExpressionAttributeValues: {
    ":start_yr": 1950,
    ":end_yr": 1959,
  },
};

console.log("Scanning Movies table.");
docClient.scan(params, onScan);

function onScan(err, data) {
  if (err) {
    console.error(
      "Unable to scan the table. Error JSON:",
      JSON.stringify(err, null, 2)
    );
  } else {
    // print all the movies
    console.log("Scan succeeded.");
    data.Items.forEach(function (movie) {
      console.log(
        movie.year + ": ",
        movie.title,
        "- rating:",
        movie.info.rating
      );
    });

    // continue scanning if we have more movies, because
    // scan can retrieve a maximum of 1MB of data
    if (typeof data.LastEvaluatedKey != "undefined") {
      console.log("Scanning for more...");
      params.ExclusiveStartKey = data.LastEvaluatedKey;
      docClient.scan(params, onScan);
    }
  }
}
// snippet-end:[dynamodb.JavaScript.CodeExample.MoviesScan]
