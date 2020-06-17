//Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

/* ABOUT THIS NODE.JS SAMPLE:
Purpose:
Inputs:
Running the code:

[Outputs | Returns]:
*/
// snippet-start:[dynamodb.javascript.trydax.02-write-data]

const AmazonDaxClient = require('amazon-dax-client');
var AWS = require("aws-sdk");

var region = "us-west-2";

AWS.config.update({
  region: region
});

var ddbClient = new AWS.DynamoDB.DocumentClient()

var tableName = "TryDaxTable";


var someData = "X".repeat(1000);
var pkmax = 10;
var skmax = 10;

for (var ipk = 1; ipk <= pkmax; ipk++)  {

    for (var isk = 1; isk <= skmax; isk++) {
        var params = {
            TableName: tableName,
            Item: {
                "pk": ipk,
                "sk": isk,
                "someData": someData
            }
        };

        //
        //put item

        ddbClient.put(params, function(err, data) {
            if (err) {
               console.error("Unable to write data: ", JSON.stringify(err, null, 2));
            } else {
               console.log("PutItem succeeded");
            }
        });

    }
}


// snippet-end:[dynamodb.javascript.trydax.02-write-data]
