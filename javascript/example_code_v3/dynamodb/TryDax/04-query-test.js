
// snippet-start:[dynamodb.javascript.trydax.04-query-test]

//Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

/* ABOUT THIS NODE.JS SAMPLE:
Purpose:
Inputs:
Running the code:

[Outputs | Returns]:
*/
const AmazonDaxClient = require('amazon-dax-client');
var AWS = require("aws-sdk");

var region = "us-west-2";

AWS.config.update({
  region: region
});

var ddbClient = new AWS.DynamoDB.DocumentClient()
var daxClient = null;

if (process.argv.length > 2) {
    var dax = new AmazonDaxClient({endpoints: [process.argv[2]], region: region})
    daxClient = new AWS.DynamoDB.DocumentClient({service: dax });
}

var client = daxClient != null ? daxClient : ddbClient;
var tableName = "TryDaxTable";

var pk = 5;
var sk1 = 2;
var sk2 = 9;
var iterations = 5;

var params = {
    TableName: tableName,
    KeyConditionExpression: "pk = :pkval and sk between :skval1 and :skval2",
    ExpressionAttributeValues: {
        ":pkval":pk,
        ":skval1":sk1,
        ":skval2":sk2
    }
};

for (var i = 0; i < iterations; i++) {
    var startTime = new Date().getTime();

    client.query(params, function(err, data) {
        if (err) {
            console.error("Unable to read item. Error JSON:", JSON.stringify(err, null, 2));
        } else {
            // Query succeeded
        }
    });

    var endTime = new Date().getTime();
    console.log("\tTotal time: ", (endTime - startTime) , "ms - Avg time: ", (endTime - startTime) / iterations, "ms");

}

// snippet-end:[dynamodb.javascript.trydax.04-query-test]
