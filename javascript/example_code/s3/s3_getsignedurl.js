// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html
// Load the AWS SDK for Node.js

var AWS = require('aws-sdk');
var fs = require('fs');
var path = require('path');

// Set the region 
// It is a good practice to get region from environment variable
var region = process.env.AWS_REGION;
AWS.config.update({region: region});

// Create S3 service object
var s3 = new AWS.S3({apiVersion: '2006-03-01'});



var uploadParams = {Bucket: process.argv[2], Key: '', Body: ''};
var file = process.argv[3];

var fileStream = fs.createReadStream(file);

fileStream.on('error', function(err) {
  console.log('File Error', err);
  process.exit(1);
});

uploadParams.Body = fileStream;
uploadParams.Key = path.basename(file);

// call S3 to retrieve upload file to specified bucket
s3.upload (uploadParams, function (err, data) {
  if (err) {
    console.log("Upload Error", err);
    process.exit(1);
  } if (data) {
    console.log("Upload Success", data.Location);

    // Generate Signed Url for the Uploaded File
    // Expiry could optionally be passed as a parameter. Generated link will expire after specified limit (seconds)
    // Defaults to 900 seconds, or 15 minutes
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
