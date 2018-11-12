 
//snippet-sourcedescription:[s3_setcors.js demonstrates how to set the CORS configuration of an Amazon S3 bucket]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[daviddeyo]


// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-configuring-buckets.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create S3 service object
s3 = new AWS.S3({apiVersion: '2006-03-01'});

var thisConfig = {
  AllowedHeaders:["Authorization"],
  AllowedMethods:[],
  AllowedOrigins:["*"],
  ExposeHeaders:[],
  MaxAgeSeconds:3000
};

// assemble allowed methods parameter based on command line parameters
var allowedMethods = [];
process.argv.forEach(function (val, index, array) {
  if (val.toUpperCase() === "POST") {allowedMethods.push("POST")};
  if (val.toUpperCase() === "GET") {allowedMethods.push("GET")};
  if (val.toUpperCase() === "PUT") {allowedMethods.push("PUT")};
  if (val.toUpperCase() === "PATCH") {allowedMethods.push("PATCH")};
  if (val.toUpperCase() === "DELETE") {allowedMethods.push("DELETE")};
  if (val.toUpperCase() === "POST") {allowedMethods.push("POST")};
});

// copy the array of allowed methods into the config object
thisConfig.AllowedMethods = allowedMethods;
// create array of configs then add the config object to it
var corsRules = new Array(thisConfig);

// create CORS params
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
