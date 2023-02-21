/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { createInterface } from "readline";

// snippet-start:[javascript.v3.utils.prompt]
export const select = (options, question = "", autoSelect) => {
  const rl = createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  const selectionInvalid = (selected) =>
    isNaN(selected) || selected < 1 || selected > options.length;
  const optionsList = options.map((opt, i) => `${i + 1}) ${opt}`).join("\n");
  const prompt = `${question}\n${optionsList}\n-> `;

  return new Promise((resolve) => {
    console.log(prompt);
    console.log(optionsList);
    if (!selectionInvalid(autoSelect)) {
      resolve([autoSelect - 1, options[autoSelect - 1]]);
      return;
    }

    rl.question(prompt, (answer) => {
      rl.close();
      const selected = parseInt(answer);
      if (!selectionInvalid(selected)) {
        console.log(
          `Invalid option. Select a number between 1 and ${options.length}`
        );
        resolve(select(options));
      } else {
        resolve([selected - 1, options[selected - 1]]);
      }
    });
  });
};
// snippet-end:[javascript.v3.utils.prompt]
