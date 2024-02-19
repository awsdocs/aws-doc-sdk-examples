/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { createInterface } from "readline";

// snippet-start:[javascript.v3.utils.promptToSelect]
export const promptToSelect = (options, question = "", autoSelect) => {
  const rl = createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  const selectionInvalid = (selected) =>
    isNaN(selected) || selected < 1 || selected > options.length;
  const optionsList = options.map((opt, i) => `${i + 1}) ${opt}`).join("\n");
  const prompt = `${question}\n${optionsList}\n-> `;

  return new Promise((resolve) => {
    if (!selectionInvalid(autoSelect)) {
      resolve([autoSelect - 1, options[autoSelect - 1]]);
      return;
    }

    rl.question(prompt, (answer) => {
      rl.close();
      const selected = parseInt(answer);
      if (selectionInvalid(selected)) {
        console.log(
          `Invalid option. Select a number between 1 and ${options.length}`
        );
        resolve(promptToSelect(options));
      } else {
        resolve([selected - 1, options[selected - 1]]);
      }
    });
  });
};
// snippet-end:[javascript.v3.utils.promptToSelect]

// snippet-start:[javascript.v3.utils.promptToContinue]
export const promptToContinue = () => {
  const rl = createInterface({
    input: process.stdin,
    output: process.stdout,
  });

  return new Promise((resolve) => {
    rl.question(`\nPress enter to continue.\n`, () => {
      rl.close();
      resolve();
    });
  });
};
// snippet-end:[javascript.v3.utils.promptToContinue]

// snippet-start:[javascript.v3.utils.promptForText]
export const promptForText = (question) => {
  const rl = createInterface({
    input: process.stdin,
    output: process.stdout,
  });

  return new Promise((resolve) => {
    rl.question(`${question}\n-> `, (answer) => {
      rl.close();
      resolve(answer);
    });
  });
};
// snippet-end:[javascript.v3.utils.promptForText]
