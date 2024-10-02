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

/**
 * @typedef {import("node:util").ParseArgsConfig} ParseArgsConfig
 * @typedef {ReturnType<import("node:util").parseArgs>} ParsedResults
 *
 * @param {import("node:util").ParseArgsConfig} config
 * @param {ParsedResults} results
 * @returns {{ errors: string[] | null }}
 */
export const validateArgs = (config, results) => {
  if (!config.options) {
    return {};
  }

  /** @type {string[] | null} */
  let errors = null;

  for (const option in config.options) {
    const optionRequired = config.options[option]?.required;
    const optionPresent = Object.hasOwn(results.values, option);

    if (optionRequired && !optionPresent) {
      errors = errors ?? [];
      errors.push(`Missing required argument "${option}".`);
    }

    return { errors };
  }
};
