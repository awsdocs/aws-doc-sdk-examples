/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic

Purpose:
uploadArchive.js demonstrates how to upload an archive to Amazon S3 Glacier.

Inputs (into code):
- REGION
- VAULT_NAME

Running the code:
node uploadArchive.js
 */

// snippet-start:[glacier.JavaScript.upload.uploadArchive]
// Load the SDK for JavaScript
const {Glacier, UploadArchiveCommand} = require("@aws-sdk/client-glacier");

// Set the AWS Region
const REGION = 'REGION'; // e.g. 'us-east-1'

// Set the parameters
const vaultname = 'VAULT_NAME'; // VAULT_NAME

// Create a new service object and buffer
const  buffer = new Buffer.alloc(2.5 * 1024 * 1024); // 2.5MB buffer
var params = {vaultName: vaultname, body: buffer};

// Instantiate a Glacier client
const glacier = new Glacier(REGION);

const run = async () => {
    try{
        const data = await glacier.send(new UploadArchiveCommand(params));
        console.log("Archive ID", data.archiveId);
    }
    catch(err){
        console.log("Error uploading archive!", err);
    }
};
run();
// snippet-end:[glacier.JavaScript.upload.uploadArchive]
exports.run = run;
