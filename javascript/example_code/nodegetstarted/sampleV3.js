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

// snippet-sourcedescription:[sample.js demonstrates how to create an S3 bucket with a unique name and upload an item to it.]
// snippet-service:[Amazon S3]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Node.js]
// snippet-keyword:[CreateBucket command]
// snippet-keyword:[PutObject command]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-27]
// snippet-sourceauthor:[Doug-AWS]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript V3 Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html#getting-started-nodejs-js-code

// snippet-start:[s3.js.create_unique_bucket]
(async function() {
  // Load the S3 client and commands for Node.js
  const {
    S3Client,
    CreateBucketCommand,
    PutObjectCommand
  } = require('@aws-sdk/client-s3')
  
  var uuid = require('uuid')

  // Unique bucket name
  const bucketName = 'node-sdk-sample-' + uuid.v4()
  // Name for uploaded object
  const keyName = 'hello_world.txt'

  const client = new S3Client({})

  const createCommand = new CreateBucketCommand({
    Bucket: bucketName
  })

  const putCommand = new PutObjectCommand({
    Bucket: bucketName,
    Key: keyName,
    Body: 'Hello World!'
  })

  try {
    await client.send(createCommand)
    await client.send(putCommand)
    console.log('Successfully uploaded data to ' + bucketName + '/' + keyName)
  } catch (err) {
    console.error(err, err.stack)
  }
})()
// snippet-end:[s3.js.create_unique_bucket]
