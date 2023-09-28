/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.wkflw.topicsandqueues.prompter]
import { select, input, confirm, checkbox } from "@inquirer/prompts";

export class Prompter {
  spacer() {
    console.log("\n");
  }

  /**
   * @param {{ message: string, choices: { name: string, value: string }[]}} options
   */
  select(options) {
    this.spacer();
    return select(options);
  }

  /**
   * @param {{ message: string }} options
   */
  input(options) {
    this.spacer();
    return input(options);
  }

  /**
   * @param {string} prompt
   */
  checkContinue = async (prompt = "") => {
    const prefix = prompt && prompt + " ";
    let ok = await this.confirm({
      message: `${prefix}Continue?"}`,
    });
    if (!ok) throw new Error("Exiting...");
  };

  /**
   * @param {{ message: string }} options
   */
  confirm(options) {
    this.spacer();
    return confirm(options);
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

  /**
   * @param {{ message: string, choices: { name: string, value: string }[]}} options
   */
  checkbox(options) {
    this.spacer();
    return checkbox(options);
  }
}
// snippet-end:[javascript.v3.wkflw.topicsandqueues.prompter]
