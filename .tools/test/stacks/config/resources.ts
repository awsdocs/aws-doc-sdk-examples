// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as fs from "fs";
import { parse } from "yaml";

interface ResourceConfig {
  topic_name: string;
  bucket_name: string;
  admin_acct: string;
  aws_region: string;
  s3_bucket_name_prefix: string;
}

export function readResourceConfig(filePath: string): ResourceConfig {
  try {
    const fileContents = fs.readFileSync(filePath, "utf8");
    const data: ResourceConfig = parse(fileContents);

    // Validate the required fields
    if (
      !data.topic_name ||
      !data.bucket_name ||
      !data.admin_acct ||
      !data.aws_region ||
      !data.s3_bucket_name_prefix
    ) {
      throw new Error(
        "Validation failed: Missing required AWS configuration fields.",
      );
    }

    return data;
  } catch (error) {
    console.error("Failed to read or parse the AWS YAML file:", error);
    throw error;
  }
}
