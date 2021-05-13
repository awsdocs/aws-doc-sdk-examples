/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-bucket-policies.html.
Purpose:
s3_getbucketpolicy.js demonstrates how to retrieve the policy of an Amazon S3 bucket.
Inputs (replace in code):
- REGION
- BUCKET_NAME
Running the code:
nodes3_getbucketpolicy.js
*/
// snippet-start:[s3.JavaScript.policy.getBucketPolicyV3]
// Import required AWS SDK clients and commands for Node.js
import { GetBucketPolicyCommand } from "@aws-sdk/client-s3";
 import { s3 } from "./libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.

// Create the parameters for calling
const bucketParams = { Bucket: "BUCKET_NAME" };

const run = async () => {
  try {
    const data = await s3.send(new GetBucketPolicyCommand(bucketParams));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.getBucketPolicyV3]
// For unit testing only.
// module.exports ={run, bucketParams};
