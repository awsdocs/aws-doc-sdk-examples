/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-access-permissions.html.

Purpose:
s3_putbucketacl.js demonstrates how to attach Access Control List (ACL) permissions to an Amazon
Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- BUCKET_NAME
- GRANTEE_1
- GRANTEE_2

Running the code:
nodes3_putbucketacl.js

Outputs:
Applies an ACL to an Amazon S3 bucket.
*/
//snippet-start:[s3.JavaScript.perms.putBucketAclV3]
// Import required AWS SDK clients and commands for Node.js.
import { PutBucketAclCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

// Set the parameters. For more information,
// see https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/S3.html#putBucketAcl-property.
export const bucketParams = {
  Bucket: "BUCKET_NAME",
  // 'GrantFullControl' allows grantee the read, write, read ACP, and write ACL permissions on the bucket.
  // Use a canonical user ID for an AWS account, formatted as follows:
  // id=002160194XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXa7a49125274
  GrantFullControl: "GRANTEE_1",
  // 'GrantWrite' allows grantee to create, overwrite, and delete any object in the bucket.
  // For example, 'uri=http://acs.amazonaws.com/groups/s3/LogDelivery'
  GrantWrite: "GRANTEE_2",
};

export const run = async () => {
  try {
    const data = await s3Client.send(new PutBucketAclCommand(bucketParams));
    console.log("Success, permissions added to bucket", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
//snippet-end:[s3.JavaScript.perms.putBucketAclV3]
// For unit testing only.
// module.exports ={run, bucketParams};
