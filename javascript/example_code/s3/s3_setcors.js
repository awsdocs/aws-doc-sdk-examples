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

//snippet-sourcedescription:[s3_setcors.js demonstrates how to set the CORS configuration of an Amazon S3 bucket]
//snippet-service:[s3]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-configuring-buckets.html

// snippet-start:[s3.JavaScript.cors.putBucketCors]
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create S3 service object
s3 = new AWS.S3({apiVersion: '2006-03-01'});

// Create initial parameters JSON for putBucketCors
var thisConfig = {
  AllowedHeaders:["Authorization"],
  AllowedMethods:[],
  AllowedOrigins:["*"],
  ExposeHeaders:[],
  MaxAgeSeconds:3000
};

// Assemble the list of allowed methods based on command line parameters
var allowedMethods = [];
process.argv.forEach(function (val, index, array) {
  if (val.toUpperCase() === "POST") {allowedMethods.push("POST")};
  if (val.toUpperCase() === "GET") {allowedMethods.push("GET")};
  if (val.toUpperCase() === "PUT") {allowedMethods.push("PUT")};
  if (val.toUpperCase() === "PATCH") {allowedMethods.push("PATCH")};
  if (val.toUpperCase() === "DELETE") {allowedMethods.push("DELETE")};
  if (val.toUpperCase() === "HEAD") {allowedMethods.push("HEAD")};
});

// Copy the array of allowed methods into the config object
thisConfig.AllowedMethods = allowedMethods;
// Create array of configs then add the config object to it
var corsRules = new Array(thisConfig);

// Create CORS params
var corsParams = {Bucket: process.argv[2], CORSConfiguration: {CORSRules: corsRules}};

// set the new CORS configuration on the selected bucket
s3.putBucketCors(corsParams, function(err, data) {
  if (err) {
    // display error message
    console.log("Error", err);
  } else {
    // update the displayed CORS config for the selected bucket
    console.log("Success", data);
  }
});
// snippet-end:[s3.JavaScript.cors.putBucketCors]
