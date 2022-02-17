/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-access-permissions.html.

Purpose:
s3_getbucketacl.js demonstrates how to retrieve the Access Control List (ACL) permissions of an Amazon
Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- BUCKET_NAME

Running the code:
nodes3_getbucketacl.js

Outputs:
Retrieves the details of the ACL permissions of an Amazon S3 bucket.
*/
//snippet-start:[s3.JavaScript.perms.getBucketAclV3]
// Import required AWS SDK clients and commands for Node.js.
import { GetBucketAclCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

// Create the parameters.
export const bucketParams = { Bucket: "BUCKET_NAME" };

export const run = async () => {
  try {
    const data = await s3Client.send(new GetBucketAclCommand(bucketParams));
    console.log("Success", data.Grants);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.perms.getBucketAclV3]
// For unit testing only.
// module.exports ={run, bucketParams};
