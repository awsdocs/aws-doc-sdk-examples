/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.wkflw.sns.prompter]
import { select, input, confirm } from "@inquirer/prompts";

export class Prompter {
  /**
   * @param {{ message: string, choices: { name: string, value: string }[]}} options
   */
  select(options) {
    return select(options);
  }

  /**
   * @param {{ message: string }} options
   */
  input(options) {
    return input(options);
  }

  /**
   * @param {{ message: string }} options
   */
  confirm(options) {
    return confirm(options);
  }
}
// snippet-end:[javascript.v3.wkflw.sns.prompter]
