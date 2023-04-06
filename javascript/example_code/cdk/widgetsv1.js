//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[Contains code for initial version of widgets.js.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-keyword:[CDK V0.24.1]
//snippet-keyword:[JavaScript]
//snippet-sourcesyntax:[javascript]
//snippet-service:[cdk]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[2019-2-8]
// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
//snippet-start:[cdk.typescript.widgets.exports_main_v1]

import {
  S3Client,
  ListObjectsV2Command,
} from '@aws-sdk/client-s3';

// In the following code we are using AWS JS SDK v3
// See https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html
const S3 = new S3Client({});
const bucketName = process.env.BUCKET;

exports.main = async function(event, context) {
  try {
    const method = event.httpMethod;

    if (method === "GET") {
      if (event.path === "/") {
        const data = await S3.send(new ListObjectsV2Command({ Bucket: bucketName }));
        const body = {
          widgets: data.Contents.map(function(e) { return e.Key })
        };
        return {
          statusCode: 200,
          headers: {},
          body: JSON.stringify(body)
        };
      }
    }

    // We only accept GET for now
    return {
      statusCode: 400,
      headers: {},
      body: "We only accept GET /"
    };
  } catch(error) {
    const body = error.stack || JSON.stringify(error, null, 2);
    return {
      statusCode: 400,
        headers: {},
        body: JSON.stringify(body)
    }
  }
}
//snippet-end:[cdk.typescript.widgets.exports_main_v1]
