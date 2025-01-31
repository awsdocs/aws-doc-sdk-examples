// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import readline from "node:readline";

import { parseString } from "@aws-doc-sdk-examples/lib/utils/util-string.js";

export const log = (str) => {
  const parsed = parseString(str);
  console.log(parsed);
  return parsed;
};

export const logger = {
  log: console.log,
  warn: (message) => {
    console.warn(`[WARNING] ${message}`);
  },
  debug: (message) => {
    console.debug(`[DEBUG ${new Date().toISOString()}] ${message}`);
  },
  error: (message) => {
    console.error(`[ERROR] ${message}`);
  },
};

export class ProgressBar {
  /**
   * Create a progress bar that will display in the console.
   * @param {Object} config
   * @param {string} config.description - The text that will display next to the progress bar.
   * @param {number} config.barLength - The length, in characters, of the progress bar.
   */
  constructor({ description, barLength }) {
    this._currentProgress = 0;
    this._barLength = barLength;
    this._description = description;
  }

  /**
   * @param {{ current: number, total: number }} event
   */
  update({ current, total }) {
    this._currentProgress = current / total;
    this._render();
  }

  _render() {
    readline.cursorTo(process.stdout, 0);
    readline.clearLine(process.stdout, 0);

    const filledLength = Math.round(this._barLength * this._currentProgress);
    const bar =
      "█".repeat(filledLength) + " ".repeat(this._barLength - filledLength);

    process.stdout.write(
      `${this._description} [${bar}] ${this._currentProgress * 100}%`,
    );

    if (this._currentProgress === 1) {
      process.stdout.write("\n");
    }
  }
}
