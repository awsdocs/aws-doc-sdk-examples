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

// snippet-sourcedescription:[initiateMultipartUpload.js demonstrates how to create a multipart upload out of 1 megabyte chunks of a Buffer object]
// snippet-service:[glacier]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Amazon S3 Glacier]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS-JSDG]

// snippet-start:[glacier.JavaScript.multipartUpload.initiateMultipartUpload]
// Create a new service object and some supporting variables
var glacier = new AWS.Glacier({apiVersion: '2012-06-01'}),
    vaultName = 'YOUR_VAULT_NAME',
    buffer = new Buffer(2.5 * 1024 * 1024), // 2.5MB buffer
    partSize = 1024 * 1024, // 1MB chunks,
    numPartsLeft = Math.ceil(buffer.length / partSize),
    startTime = new Date(),
    params = {vaultName: vaultName, partSize: partSize.toString()};

// Compute the complete SHA-256 tree hash so we can pass it
// to completeMultipartUpload request at the end
var treeHash = glacier.computeChecksums(buffer).treeHash;

// Initiate the multipart upload
console.log('Initiating upload to', vaultName);
// Call Glacier to initiate the upload.
glacier.initiateMultipartUpload(params, function (mpErr, multipart) {
    if (mpErr) { console.log('Error!', mpErr.stack); return; }
    console.log("Got upload ID", multipart.uploadId);

    // Grab each partSize chunk and upload it as a part
    for (var i = 0; i < buffer.length; i += partSize) {
        var end = Math.min(i + partSize, buffer.length),
            partParams = {
                vaultName: vaultName,
                uploadId: multipart.uploadId,
                range: 'bytes ' + i + '-' + (end-1) + '/*',
                body: buffer.slice(i, end)
            };

        // Send a single part
        console.log('Uploading part', i, '=', partParams.range);
        glacier.uploadMultipartPart(partParams, function(multiErr, mData) {
            if (multiErr) return;
            console.log("Completed part", this.request.params.range);
            if (--numPartsLeft > 0) return; // complete only when all parts uploaded

            var doneParams = {
                vaultName: vaultName,
                uploadId: multipart.uploadId,
                archiveSize: buffer.length.toString(),
                checksum: treeHash // the computed tree hash
            };

            console.log("Completing upload...");
            glacier.completeMultipartUpload(doneParams, function(err, data) {
                if (err) {
                    console.log("An error occurred while uploading the archive");
                    console.log(err);
                } else {
                    var delta = (new Date() - startTime) / 1000;
                    console.log('Completed upload in', delta, 'seconds');
                    console.log('Archive ID:', data.archiveId);
                    console.log('Checksum:  ', data.checksum);
                }
            });
        });
    }
});
// snippet-end:[glacier.JavaScript.multipartUpload.initiateMultipartUpload]
