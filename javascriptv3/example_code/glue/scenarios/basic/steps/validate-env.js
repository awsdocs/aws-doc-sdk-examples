/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { log } from "../log.js";

const keys = [
  "BUCKET_NAME",
  "ROLE_NAME",
  "PYTHON_SCRIPT_KEY",
  "S3_TARGET_PATH",
  "DATABASE_NAME",
  "TABLE_PREFIX",
  "TABLE_NAME",
  "CRAWLER_NAME",
  "JOB_NAME",
];

const validateEnv = async (context) => {
  log("Checking if environment variables exist.");

  if (!context || !process.env) {
    throw new Error("Missing context.");
  }

  keys.forEach((key) => {
    if (!process.env[key]) {
      throw new Error(`Missing environment variable. No value for ${key}.`);
    }
  });

  log("Variables exist.", { type: "success" });
  return { ...context };
};

export { validateEnv, keys };
