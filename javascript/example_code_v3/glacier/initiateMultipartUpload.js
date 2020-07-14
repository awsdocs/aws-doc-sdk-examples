/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/glacier-example-multipart-upload.html.

Purpose:
initiateMultipartUpload.js demonstrates how to create a multipart upload out of 1 megabyte chunks of a Buffer object.

Inputs (into code):
- REGION
- VAULT_NAME

Running the code:
node createVault.js
 */
// snippet-start:[glacier.JavaScript.multipartUpload.initiateMultipartUpload]
// Create a new service object and some supporting variables
// Load the SDK for JavaScript
const {Glacier, InitiateMultipartUploadCommand, UploadMultipartPartCommand, CompleteMultipartUploadCommand } = require("@aws-sdk/client-glacier");

// Set the AWS Region
const REGION = "REGION"; //e.g., 'us-east-1'

// Set the parameters
const vaultname = 'VAULT_NAME'; //VAULT_NAME
const buffer = new Buffer.alloc(2.5 * 1024 * 1024); // 2.5MB buffer
const partSize = 1024 * 1024;// 1MB chunks,
const numPartsLeft = Math.ceil(buffer.length / partSize);
const startTime = new Date();
const params = {vaultName: vaultname, partSize: partSize.toString()};

// Compute the complete SHA-256 tree hash so we can pass it
// to completeMultipartUpload request at the end
var treeHash = glacier.computeChecksums(buffer).treeHash;

// Instantiate a Glacier client
const glacier = new Glacier({ region: REGION });

const run = async () => {
    try {
        const multipart = await glacier.send(new InitiateMultipartUploadCommand(params));
        console.log("Got upload ID", multipart.uploadId);
        try {
            // Grab each partSize chunk and upload it as a part
            for (var i = 0; i < buffer.length; i += partSize) {
                var end = Math.min(i + partSize, buffer.length),
                    partParams = {
                        vaultName: vaultname,
                        uploadId: multipart.uploadId,
                        range: 'bytes ' + i + '-' + (end - 1) + '/*',
                        body: buffer.slice(i, end)
                    };
            }
            console.log('Uploading part', i, '=', partParams.range);
            const mData = await glacier.send(new UploadMultipartPartCommand(partParams));
            console.log("Completed part", this.request.params.range);
            try {
                var doneParams = {
                    vaultName: vaultname,
                    uploadId: multipart.uploadId,
                    archiveSize: buffer.length.toString(),
                    /*
                                checksum: treeHash // the computed tree hash
                    */
                };
                console.log("Completing upload...");
                const data = await glacier.send(new CompleteMultipartUploadCommand(doneParams));
                var delta = (new Date() - startTime) / 1000;
                console.log('Completed upload in', delta, 'seconds');
                console.log('Archive ID:', data.archiveId);
                console.log('Checksum:  ', data.checksum);
            } catch (err) {
                console.log("An error occurred while completing the upload");
            }
        } catch (err) {
            console.log("An error occurred while uploading the archive");
            return;

        }
    } catch (err) {
        console.log('Error!', err.stack);
        return
    }
};
run();
// snippet-end:[glacier.JavaScript.multipartUpload.initiateMultipartUpload]
exports.run = run; //for unit tests only
