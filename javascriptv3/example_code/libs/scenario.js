/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { Prompter } from "./prompter.js";
import { Logger } from "./logger.js";
import { SlowLogger } from "./slow-logger.js";

class Step {
  /**
   * @param {string} name
   */
  constructor(name) {
    this.name = name;
  }
}

export class ScenarioOutput extends Step {
  /**
   * @param {string} name
   * @param {string | (context: Record<string, any>) => string} value
   * @param {{ slow: boolean }} options
   */
  constructor(name, value, options = { slow: true }) {
    super(name);
    this.value = value;
    this.options = options;
    this.slowLogger = new SlowLogger(20);
    this.logger = new Logger();
  }

  /**
   * @param {Record<string, any>} context
   */
  async handle(context) {
    const output =
      typeof this.value === "function" ? this.value(context) : this.value;
    const logger = this.options.slow ? this.slowLogger : this.logger;
    await logger.log(JSON.stringify(output));
  }
}

export class ScenarioInput extends Step {
  /**
   * @param {string} name
   * @param {string} prompt
   * @param {{ type: "input" | "multi-select" | "select", choices: { name: string, value: string }[]} options
   */
  constructor(name, prompt, options) {
    super(name);
    this.prompt = prompt;
    this.options = options;
    this.prompter = new Prompter();
  }

  /**
   * @param {Record<string, any>} context
   */
  async handle(context) {
    if (this.options.type === "multi-select") {
      context[this.name] = await this.prompter.checkbox({
        message: this.prompt,
        choices: this.options.choices,
      });
    } else if (this.options.type === "select") {
      context[this.name] = await this.prompter.select({
        message: this.prompt,
        choices: this.options.choices,
      });
    } else if (this.options.type === "input") {
      context[this.name] = await this.prompter.input({ message: this.prompt });
    } else {
      throw new Error(
        `Error handling ScenarioInput, ${this.options.type} is not supported.`,
      );
    }
  }
}

export class ScenarioAction extends Step {
  /**
   *
   * @param {string} name
   * @param {(context: Record<string, any>) => Promise<void>} action
   */
  constructor(name, action) {
    super(name);
    this.action = action;
  }

  async handle(context) {
    await this.action(context);
  }
}

export class Scenario {
  /**
   * @type {Record<string, any>}
   */
  context = {};

  /**
   * @param {(ScenarioOutput | ScenarioInput | ScenarioAction)[]} steps
   */
  constructor(steps = []) {
    this.steps = steps;
  }

  async run() {
    for (const step of this.steps) {
      await step.handle(this.context);
    }
  }
}
