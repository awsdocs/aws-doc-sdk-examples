/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[s3.JavaScript.buckets.createclientv3]
import { S3Client } from "@aws-sdk/client-s3";
// This relies on a Region being set up in your local AWS config.
const client = new S3Client({});
export { client };
// snippet-end:[s3.JavaScript.buckets.createclientv3]
