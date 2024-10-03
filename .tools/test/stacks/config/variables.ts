// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as fs from "fs";
import { parse } from "yaml";

interface VariableConfig {
  s3_bucket_name_prefix: string;
}

export function readVariableConfig(filePath: string): VariableConfig {
  try {
    const fileContents = fs.readFileSync(filePath, "utf8");
    const data: VariableConfig = parse(fileContents);
    console.log(data);

    // Validate the required field
    if (!data.s3_bucket_name_prefix) {
      throw new Error("Validation failed: Missing required 's3_bucket_name_prefix' field.");
    }

    return data;
  } catch (error) {
    console.error("Failed to read or parse the YAML file:", error);
    throw error;
  }
}
