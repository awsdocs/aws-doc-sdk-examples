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

//snippet-sourcedescription:[s3_getsignedurl.js demonstrates how to generate a presigned URL to download an Amazon S3 object.]
//snippet-service:[s3]
//snippet-keyword:[JavaScript]
//snippet-sourcesyntax:[javascript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-08-06]
//snippet-sourceauthor:[romanbalayan]


// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
var fs = require('fs');
var path = require('path');

// Set the region 
// It is a good practice to get region from environment variable
var region = process.env.AWS_REGION;
AWS.config.update({region: region});

// Define the upload parameters
var uploadParams = {Bucket: process.argv[2], Key: '', Body: ''};
var file = process.argv[3];

var fileStream = fs.createReadStream(file);
fileStream.on('error', function(err) {
  console.log('File Error', err);
  process.exit(1);
});
uploadParams.Body = fileStream;
uploadParams.Key = path.basename(file);

// Upload the file
var s3 = new AWS.S3({apiVersion: '2006-03-01'});
s3.upload (uploadParams, function (err, data) {
  if (err) {
    console.log("Upload Error", err);
    process.exit(1);
  } if (data) {
    console.log("Upload Success", data.Location);

    // Generate a presigned URL for someone else to download the file.
    // Generated URL will expire after the specified number of seconds.
    // Set 1 hour expiry: 60*60 seconds = 3600 seconds

    var getSignedUrlParams = {
    	Bucket: uploadParams.Bucket,
    	Key: uploadParams.Key,
    	Expires: 3600
    };

    s3.getSignedUrl('getObject', getSignedUrlParams, function(err, url) {
    	if(err) {
    		console.log("Error getting Signed Url: ", err);
    		process.exit(1);
    	}
    	console.log("Generated Signed Url: ", url);
    });
  }
});
