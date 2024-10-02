// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { fileURLToPath } from "url";

export const getEnv = (/** @type {string} */ key) => process.env[key];
export const setEnv = (/** @type {string} */ key, value) => {
  process.env[key] = value;
};

/**
 * Check if the running file was run directly.
 * @param {string | URL} fileUrl
 */
export const isMain = (fileUrl) => process.argv[1] === fileURLToPath(fileUrl);
