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
  storage?: number; // GiB, 20GiB to 200GiB
}

type AccountConfigYaml = {
  [K in keyof AccountConfig]: string
}

interface AccountConfigs {
  [key: string]: AccountConfig;
}

export function readAccountConfig(filePath: string): AccountConfigs {
  try {
    const fileContents = fs.readFileSync(filePath, "utf8");
    const data = Object.entries(parse(fileContents) as Record<string, AccountConfigYaml>).reduce((data, [name, config]) => {
      const {account_id, status, vcpus, memory, storage} = config;
      if (!account_id) {
        throw new Error(`Validation failed: Missing account_id field in ${name}`);
      }
      switch (status) {
        case "enabled": // fallthrough
        case "disabled":
          break;
        default:
          throw new Error(`Validation failed: invalid status ${status} in ${name}`) 
      }
      data[name] = {
        account_id,
        status,
        vcpus,
        memory,
        storage: numberOrDefault(storage, 20),
      }
      return data;
    }, {} as Record<string, AccountConfig>)

    return data;
  } catch (error) {
    console.error("Failed to read or parse the YAML file:", { error });
    throw error;
  }
}

function numberOrDefault(storage: string | undefined, defaultValue: number) {
  const batchStorage = Number(storage);
  const batchStorageNumber = isNaN(batchStorage) ? defaultValue : batchStorage;
  return batchStorageNumber;
}

