/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
upload_files_to_s3.js is part of a tutorial demonstrating how to build and deploy and app to submit
data to a DynamoDB table. upload_files_to_s3.js creates a bucket, and uploads the index.html, error.html,
and main.js files to it.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID
- TABLE_NAME

Running the code:
1. Save upload_files_to_s3.js in the same folder as the index.html, error.html, and main.js files.
2. Run the following in the command line:
    node upload_files_to_s3.js
 */
// snippet-start:[s3.JavaScript.crossservice.uploadFilesV3]

const { S3, S3Client, PutBucketWebsiteCommand, PutObjectCommand } = require("@aws-sdk/client-s3");
const path = require("path");
const fs = require("fs");

// Set the AWS region
const REGION = "eu-west-1"; //e.g. "us-east-1"
// Set the bucket parameters
const bucketParams = { Bucket: "BUCKET_NAME" };
const uploadParams1= { Bucket: bucketParams.Bucket, Key: "index.html" };
const uploadParams2= { Bucket: bucketParams.Bucket, Key: "error.html" };
const uploadParams3= { Bucket: bucketParams.Bucket, Key: "main.js" };

var file1 = "index.html";
var file2 = "error.html";
var file3 = "main.js";

// Instantiate an S3 client
const s3 = new S3({});
const s3Client = new S3Client({});

//Attempt to create the bucket
const run = async () => {
    try {
        const fileStream1 = fs.createReadStream(file1);
        fileStream1.on("error", function (err) {
            console.log("File Error", err);
        });
        uploadParams1.Body = fileStream1;
        var path = require('path');
        uploadParams1.Key = path.basename(file1);
        // call S3 to retrieve upload file to specified bucket
        try {
            const data = await s3.send(new PutObjectCommand(uploadParams1));
            console.log("Success", data);
        }
        catch (err) {
            console.log("Error", err);
        }
            const fileStream2 = fs.createReadStream(file2);
            fileStream2.on("error", function (err) {
                console.log("File Error", err);
            });
        uploadParams2.Body = fileStream2;
        var path = require('path');
         uploadParams2.Key = path.basename(file2);
            // call S3 to retrieve upload file to specified bucket
            try {
                const data = await s3.send(new PutObjectCommand(uploadParams2));
                console.log("Success", data)
            }catch (err) {
                console.log("Error", err);
            }
            const fileStream3 = fs.createReadStream(file3);
            fileStream3.on("error", function (err) {
            console.log("File Error", err);
        });
        uploadParams3.Body = fileStream3;
        var path = require('path');
        uploadParams3.Key = path.basename(file3);
            // call S3 to retrieve upload file to specified bucket
                try {
                    const data = await s3.send(new PutObjectCommand(uploadParams3));
                    console.log("Success", data)
                }
                catch (err) {
                    console.log("Error", err);
                }
    } catch (err) {
            console.log("Error", err);
        }
    };
run();
// snippet-end:[s3.JavaScript.crossservice.uploadFilesV3]



