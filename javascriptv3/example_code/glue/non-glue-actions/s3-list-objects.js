/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { ListObjectsCommand, S3Client } from "@aws-sdk/client-s3";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

const s3ListObjects = (bucketName) => {
  const client = new S3Client({ region: DEFAULT_REGION });
  const command = new ListObjectsCommand({ Bucket: bucketName });
  return client.send(command);
};

export { s3ListObjects };
