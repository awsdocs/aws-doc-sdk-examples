/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html.

Purpose:
s3_setcors.js demonstrates how to set the CORS configuration of an Amazon S3 bucket.

Inputs (replace in code):
-
-

Running the code:
node s3_setcors.js
 */
// snippet-start:[s3.JavaScript.cors.putBucketCorsV3]
// Import required AWS SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Create the parameters for the bucket
const bucketParams = { Bucket: "BUCKET_NAME" };
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
const corsParams = {Bucket: bucketParams.Bucket, CORSConfiguration: {CORSRules: corsRules}};

// Create S3 service object
const s3 = new S3({});

const run = async () => {
    try {
        const data = await s3.putBucketCors(corsParams);
        console.log("Success", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.cors.putBucketCorsV3]
//for unit tests only
exports.run = run;
