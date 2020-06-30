/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html.

Purpose:
s3_upload.js uploads a file to an S3 bucket.

Inputs (in the commandline input below):
- REGION (in the commandline input below)
- BUCKET_NAME (in the commandline input below)
- KEY (in the commandline input below): The name of the file to upload.
- BODY (in the commandline input below): The contents of the uploaded file. Leave blank/remove to retain contents of original file.

Running the code:
node s3_upload_putcommand.js REGION BUCKET_NAME KEY BODY
*/
// snippet-start:[s3.JavaScript.v3.buckets.upload_putcommand]
async function run() {
// call S3 to retrieve upload file to specified bucket
    // Import required AWS SDK clients and commands for Node.js
    const { S3, PutObjectCommand } = require("@aws-sdk/client-s3");
    // Set the AWS region
    const region = process.argv[2];
    // Create S3 service object
    const s3 = new S3(region);
    // Set the parameters
    const uploadParams = {Bucket: process.argv[3], Key: process.argv[4], Body:process.argv[5]};

    try {
        const data = await s3.send(new PutObjectCommand(uploadParams));
        console.log('Successfully uploaded to ' + uploadParams.Bucket +'/'+ uploadParams.Key);
    }
    catch (err) {
        console.log('Error', err);
    }
};
run();
// snippet-end:[s3.JavaScript.v3.buckets.upload_putcommand]
//for unit tests only
exports.run = run;
python/example_code/sts/sts_temporary_credentials/federated_url.py
