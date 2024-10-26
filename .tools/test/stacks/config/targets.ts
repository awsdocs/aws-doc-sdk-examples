// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as fs from "fs";
import { parse } from "yaml";

interface AccountConfig {
  account_id: string;
  status: "enabled" | "disabled";
  // https://docs.aws.amazon.com/batch/latest/APIReference/API_ResourceRequirement.html
  vcpus?: string; // Count
  memory?: string; // MiB, but limited based on vCPU count, see docs
  storage?: string; // GiB, 20GiB to 200GiB
}

interface AccountConfigs {
  [key: string]: AccountConfig;
}

export function readAccountConfig(filePath: string): AccountConfigs {
  try {
    const fileContents = fs.readFileSync(filePath, "utf8");
    const data: AccountConfigs = parse(fileContents);

    Object.values(data).forEach((config) => {
      if (!config.account_id || !config.status) {
        throw new Error("Validation failed: Missing required account fields.");
      }
    });

    return data;
  } catch (error) {
    console.error("Failed to read or parse the YAML file:", { error });
    throw error;
  }
}
