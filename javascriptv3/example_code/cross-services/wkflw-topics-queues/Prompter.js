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
   * @param {{ message: string }} options
   */
  confirm(options) {
    this.spacer();
    return confirm(options);
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
