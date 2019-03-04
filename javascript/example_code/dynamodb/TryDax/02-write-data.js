// snippet-sourcedescription:[02-write-data.js demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.javascript.trydax.02-write-data] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
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