// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.CodeExample.MoviesLoadData]
var AWS = require("aws-sdk");
var fs = require("fs");

AWS.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000",
});

var docClient = new AWS.DynamoDB.DocumentClient();

console.log("Importing movies into DynamoDB. Please wait.");

var allMovies = JSON.parse(fs.readFileSync("moviedata.json", "utf8"));
allMovies.forEach(function (movie) {
  var params = {
    TableName: "Movies",
    Item: {
      year: movie.year,
      title: movie.title,
      info: movie.info,
    },
  };

  docClient.put(params, function (err, data) {
    if (err) {
      console.error(
        "Unable to add movie",
        movie.title,
        ". Error JSON:",
        JSON.stringify(err, null, 2)
      );
    } else {
      console.log("PutItem succeeded:", movie.title);
    }
  });
});
// snippet-end:[dynamodb.JavaScript.CodeExample.MoviesLoadData]
