// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html
//
// to run this in Git-Bash
// ******* $ node ddb_scan_Music.js  *******
//

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'eu-west-1'});

// Create DynamoDB service object
var ddb = new AWS.DynamoDB({apiVersion: '2012-08-10'});

var params = {
   TableName: "Music"
  
 };

ddb.scan(params, function(err, data) {
   if (err) console.log(err, err.stack); 	// an error occurred
   else     console.log(data.Items);    	// successful response
							   	// output items on console
});
