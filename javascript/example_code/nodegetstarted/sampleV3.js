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

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html

// snippet-start:[GettingStarted.JavaScriptV3.NodeJS.getStarted]
// Load the S3 and UUID packages
(async () => {
  const s3 = require('@aws-sdk/client-s3')
  const uuid = require('uuid')
  const s3Client = new s3.S3Client({
    region: process.argv[2]
  })

  // Create unique bucket name
  const bucketName = 'node-sdk-sample-' + uuid.v4()

  // Create name for uploaded object key
  const keyName = 'hello_world.txt'

  // Create params for CreateBucket call
  const bucketParams = { Bucket: bucketName }

  // Create params for putObject call
  const objectParams = { Bucket: bucketName, Key: keyName, Body: 'Hello World!' }

  try {
    const response = await s3Client.send(
      new s3.CreateBucketCommand(bucketParams)
    )
    const reply = await s3Client.send(
      new s3.PutObjectCommand(objectParams)
    )
    console.log('Successfully uploaded data to ' + bucketName + '/' + keyName)
  } catch (err) {
    console.error(err)
  }
})()
// snippet-end:[GettingStarted.JavaScriptV3.NodeJS.getStarted]
