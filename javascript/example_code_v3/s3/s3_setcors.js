/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-configuring-buckets.html

Purpose:
s3_setcors.js demonstrates how to set the CORS configuration of an Amazon S3 bucket.

Inputs:
- BUCKET_NAME (in command line input below)
- REGION (in command line input below)

Running the code:
node s3_setcors.js BUCKET_NAME REGION
 */
// snippet-start:[s3.JavaScript.cors.putBucketCors]
async function run(){
  try{
    // Create initial parameters JSON for putBucketCors
    const thisConfig = {
      AllowedHeaders:["Authorization"],
      AllowedMethods:[],
      AllowedOrigins:["*"],
      ExposeHeaders:[],
      MaxAgeSeconds:3000
    };
// Assemble the list of allowed methods based on command line parameters
    const allowedMethods = [];
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
    const corsRules = new Array(thisConfig);
// Create CORS params
    const corsParams = {Bucket: process.argv[2], CORSConfiguration: {CORSRules: corsRules}};
    const { S3, putBucketCors } = require("@aws-sdk/client-s3");
    const region = process.argv[3];
    const s3 = new S3(region);
    const data = await s3.putBucketCors(corsParams);
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.cors.putBucketCors]
exports.run = run;

