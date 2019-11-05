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

// snippet-sourcedescription:[s3_getcors.js demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.]
// snippet-service:[s3]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-05]
// snippet-sourceauthor:[Doug-AWS]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript V3 Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html

// snippet-start:[s3.JavaScriptV3.cors.getBucketCors]
(async () => {
  // Load AWS S3 for Node.js
  const s3 = require('@aws-sdk/client-s3')
  const s3Client = new s3.S3Client({
    region: process.argv[2]
  })

  // Set the parameters for getBucketCors
  const bucketParams = { Bucket: process.argv[3] }

  // Retrieve CORS configuration
  try {
    const response = await s3Client.send(
      new s3.GetBucketCorsCommand(bucketParams)
    )
    console.log('Success', JSON.stringify(response))
  } catch (err) {
    console.error(err)
  }
})()
// snippet-end:[s3.JavaScriptV3.cors.getBucketCors]
