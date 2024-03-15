// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as readline from "readline";

/**
 * @typedef {Object} FoundationModel
 * @property {string} modelName - The name of the model
 */

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

const ask = (question, validate, onValid, repeat) => {
  if (repeat) {
    // Overwrite previous line
    readline.moveCursor(process.stdout, 0, -1);
    readline.clearLine(process.stdout, 0);
    readline.cursorTo(process.stdout, 0);
  }

  rl.question(question, (answer) => {
    if (validate(answer)) {
      onValid(answer);
    } else {
      ask(question, validate, onValid, true);
    }
  });
};

const select = (
  /** @type {string[]} options */ options,
  text,
  validate,
  onValid,
) => {
  // Print the options
  options.forEach((option, index) => {
    console.log(`${index + 1}. ${option}`);
  });

  ask(text, validate, onValid, false);
};

export const selectModel = (/** @type {FoundationModel[]} */ models) => {
  return new Promise((resolve) => {
    const validate = (answer) => {
      if (answer === "q") return true;
      else {
        const selectedIndex = parseInt(answer, 10) - 1;
        return selectedIndex >= 0 && selectedIndex < models.length;
      }
    };

    const onValid = (answer) => {
      if (answer === "q") {
        rl.close();
        resolve(null);
      } else {
        resolve(models[parseInt(answer, 10) - 1]);
      }
    };

    const text = "Select a model number (q to quit): ";
    select(
      models.map((m) => m.modelName),
      text,
      validate,
      onValid,
    );
  });
};

export const selectNextStep = (/** @type {string} */ modelName) => {
  return new Promise((resolve) => {
    const options = [`Prompt ${modelName} again`, "Select another model"];
    const text = "Choose your next step (q to quit): ";

    const validate = (answer) => ["1", "2", "q"].includes(answer);
    const onValid = (answer) => {
      if (answer === "q") {
        rl.close();
        resolve(null);
      } else {
        resolve(answer);
      }
    };

    select(options, text, validate, onValid);
  });
};

export const askForPrompt = () => {
  return new Promise((resolve) => {
    const validate = (/** @type {string} */ answer) => answer.trim() !== "";
    const onValid = (answer) => resolve(answer);
    ask("Now, enter your prompt: ", validate, onValid);
  });
};
