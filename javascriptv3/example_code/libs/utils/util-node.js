// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { fileURLToPath } from "node:url";

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
      errors.push(`Missing required argument "--${option}".`);
    }
  }

  return { errors };
};

/**
 * Take a list of options and program info and print a man page.
 * @param {Record<string, {} extends { required?: boolean, description?: string }>} options
 * @param {{ name: string, synopsis: string, description: string }} programInfo
 */
export const printManPage = (options, programInfo) => {
  const { name, synopsis, description } = programInfo;

  console.log("NAME");
  console.log(`     ${name}`);
  console.log();
  console.log("SYNOPSIS");
  console.log(`     ${synopsis}`);
  console.log();
  console.log("DESCRIPTION");
  console.log(`     ${description}`);
  console.log();
  console.log("OPTIONS");

  const optionPadding =
    Math.max(...Object.keys(options).map((key) => key.length)) + 4;

  for (const [key, value] of Object.entries(options)) {
    const paddedKey = `--${key}`.padEnd(optionPadding);
    console.log(
      `     ${paddedKey}${value.type}${(value.description ?? "") && ` - ${value.description}`}`,
    );
  }
};
