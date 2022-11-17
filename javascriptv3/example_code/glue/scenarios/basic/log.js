/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import chalk from "chalk";

const formatterMap = {
  info: { format: chalk.blue },
  error: { format: chalk.red },
  warn: { format: chalk.yellow },
  success: { format: (msg) => `${chalk.green(msg)} ✅` },
  object: { format: (msg) => JSON.stringify(msg, null, 2) },
};

/**
 *
 * @param {string} message
 * @param {{ type: 'info' | 'error' | 'warn' | 'success' | 'object' }} options
 */
const log = (message, options = {}) => {
  const formatter = formatterMap[options.type || "info"];
  console.log(formatter.format(message));
};

export { log };
