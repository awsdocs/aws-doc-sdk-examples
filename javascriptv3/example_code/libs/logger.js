// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import wrap from "fast-word-wrap";

export class Logger {
  constructor(lineLength = 80) {
    this.lineLength = lineLength;
  }

  /**
   * @param {string} message
   */
  log(message) {
    console.log(message);
    return Promise.resolve();
  }

  /**
   * @param {{ oneLineOnly: boolean }} options
   */
  hr({ oneLineOnly } = { oneLineOnly: false }) {
    const rule = "*".repeat(this.lineLength);
    return oneLineOnly ? rule : ["\n", rule, "\n"].join("");
  }

  /**
   * @param {string} message
   */
  box(message) {
    const linePrefix = "*  ";
    const lineSuffix = "  *";

    const maxContentLength = this.lineLength - (linePrefix + lineSuffix).length;
    const chunks = message
      .split("\n")
      .map((l) => l && wrap(l, maxContentLength).split("\n"))
      .flat();

    /**
     * @param {string} c
     */
    const fill = (c) => c + " ".repeat(maxContentLength - c.length);

    /**
     * @param {string} c
     */
    const line = (c) => `${linePrefix}${fill(c)}${lineSuffix}`;

    return [this.hr(), chunks.map(line).join("\n"), this.hr()].join("");
  }

  /**
   * Log a horizontal rule to the console. If a message is provided,
   * log a section header.
   * @param {string?} message
   */
  logSeparator(message) {
    if (!message) {
      console.log(this.hr());
    } else {
      console.log(this.box(message));
    }
  }
}
