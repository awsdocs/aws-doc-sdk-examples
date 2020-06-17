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

// snippet-sourcedescription:[sample.js demonstrates how to get started using the AWS SDK for JavaScript.]
// snippet-service:[nodejs]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Node.js]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-06-02]
// snippet-sourceauthor:[AWS-JSDG]

/* ABOUT THIS NODE.JS SAMPLE:
Purpose:
This function uploads an object to an S3 bucket.
This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html.
Inputs:
- BUCKET_NAME: the name of the bucket, which you enter to run the code (see 'Running the code' below).
- KEY: The Key parameter is set to the name of the selected file (existing or not), which you enter to run the code (see 'Running the code' below).
- BODY: The Body parameter is the contents of the uploaded file. Leave blank/remove to retain contents of original file.


Running the code:
Enter the following at the command line:
node s3_upload_putcommand.js
*/
// snippet-start:[GettingStarted.JavaScript.NodeJS.getStarted]
const { PutObjectCommand } = require("@aws-sdk/client-s3");
const uploadParams = {Bucket: "BUCKET_NAME", Key: 'KEY', Body:'BODY'};

async function uploadAnObject(client, parameters) {
// call S3 to retrieve upload file to specified bucket
    try {
        const data = await client.send(new PutObjectCommand(parameters));
        console.log('Successfully uploaded to ' + uploadParams.Bucket +'/'+ uploadParams.Key);
    }
    catch (err) {
        console.log('Error', err);
    }
};

async function run(){
    // Load the AWS SDK s3 client for Node.js
    const { S3 } = require("@aws-sdk/client-s3");
    // Create S3 service object
    const s3 = new S3();
    uploadAnObject(s3, uploadParams);
}
run();
// snippet-end:[GettingStarted.JavaScript.NodeJS.getStarted]
