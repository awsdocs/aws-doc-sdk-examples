// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  Scenario,
  ScenarioAction,
  ScenarioInput,
  ScenarioOutput,
} from "./index.js";

const greet = new ScenarioOutput(
  "greet",
  "Hi! This is a scenario. It can handle your inputs and outputs.",
  { header: true },
);

const describeInput = new ScenarioOutput(
  "describe input",
  "ScenarioInput collects three different types of input from the user: " +
    "plain text, single-selection, and multi-selection. Let's look at some examples.",
);

const getFoods = new ScenarioInput("foods", "Choose your favorite foods.", {
  type: "multi-select",
  choices: ["sushi", "pizza", "hamburger"],
});

const getAge = new ScenarioInput(
  "age",
  "Select your age range (my apologies if you're younger or older than the provided ranges):",
  { type: "select", choices: ["18-30", "31-50", "51-100"] },
);

const getName = new ScenarioInput("name", "What's your name?", {
  type: "input",
});

const describeOutput = new ScenarioOutput(
  "describe output",
  "ScenarioOutput can log a raw string or build " +
    "dynamic output using the input from previous steps. " +
    "You've already seen several raw string examples in the preceding output. " +
    "The dynamic output is generated with a function that takes a state " +
    "object that tracks input values. Here's some dynamic output: ",
);

const dynamicOutput = new ScenarioOutput(
  "dynamicOutput",
  /**
   * @param {{ name: string, age: string, foods: string[] }} c
   */
  (c) =>
    `Hi, ${c.name}! You are between the ages of ${c.age}. ` +
    `Your favorite foods are: ${c.foods.join(", ")}.`,
);

const describeActions = new ScenarioOutput(
  "describe actions",
  "ScenarioActions let you run a function. The function is passed the same state " +
    "as the dynamic output. You can modify the state here if you like, do asynchronous tasks, " +
    "or anything else you'd like to do. Actions don't log anything on their own, but you should use " +
    "ScenarioOutput to do that. The next step will run a 'reverse name' action and then run the " +
    "dynamic output step again.",
);

const reverseName = new ScenarioAction(
  "reverse name",
  /**
   * @param {{ name: string }} c
   */
  (c) => {
    c.name = c.name.split("").reverse().join("");
  },
);

const summary = new ScenarioOutput(
  "summary",
  "That's all there is too it! Thanks for checking it out.",
);

const confirmExit = new ScenarioInput(
  "quit",
  "Enter anything (or nothing) to exit.",
  { type: "confirm" },
);

const myScenario = new Scenario("My Scenario", [
  greet,
  describeInput,
  getFoods,
  getAge,
  getName,
  describeOutput,
  dynamicOutput,
  describeActions,
  reverseName,
  dynamicOutput,
  summary,
  confirmExit,
]);

await myScenario.run();
