/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

export class Logger {
  /**
   * @param {string} message
   */
  log(message) {
    console.log(message);
    return Promise.resolve();
  }

  /**
   * Log a horizontal rule to the console. If a message is provided,
   * log a section header.
   * @param {string?} message
   */
  logSeparator(message) {
    if (!message) {
      console.log("\n", "*".repeat(80), "\n");
    } else {
      console.log(
        "\n",
        "*".repeat(80),
        "\n",
        "** ",
        message,
        " ".repeat(80 - message.length - 8),
        "**\n",
        "*".repeat(80),
        "\n",
      );
    }
  }
}
