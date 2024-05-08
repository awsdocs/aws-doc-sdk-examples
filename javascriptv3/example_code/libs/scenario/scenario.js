// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { Prompter } from "../prompter.js";
import { Logger } from "../logger.js";
import { SlowLogger } from "../slow-logger.js";

/**
 * @typedef {{ confirmAll: boolean, verbose: boolean }} StepHandlerOptions
 */

/**
 * @typedef {{} & StepHandlerOptions} RunConfig
 */

/**
 * @typedef {{skipWhen: (state: Record<string, any>) => boolean}} StepOptions
 */

/**
 * @template O
 */
export class Step {
  /**
   * @param {string} name
   * @param {O} [stepOptions]
   */
  constructor(name, stepOptions) {
    this.name = name;

    /**
     * @type {O & StepOptions}
     */
    this.stepOptions = {
      skipWhen: () => false,
      ...stepOptions,
    };
  }

  /**
   * @param {Record<string, any>} state,
   * @param {StepHandlerOptions} [stepHandlerOptions]
   */
  handle(state, stepHandlerOptions) {
    if (stepHandlerOptions?.verbose) {
      console.log(
        `[DEBUG ${new Date().toISOString()}] Handling step: ${
          this.constructor.name
        }<${this.name}>`
      );
      console.log(
        `[DEBUG ${new Date().toISOString()}] State: ${JSON.stringify(state)}`
      );
    }
  }
}

/**
 * @typedef {{ slow: boolean, header: boolean, preformatted: boolean }} ScenarioOutputOptions
 */

/**
 * @extends {Step<ScenarioOutputOptions}
 */
export class ScenarioOutput extends Step {
  /**
   * @param {string} name
   * @param {string | (state: Record<string, any>) => string | false} value
   * @param {ScenarioOutputOptions} [scenarioOutputOptions]
   */
  constructor(name, value, scenarioOutputOptions = { slow: true }) {
    super(name, scenarioOutputOptions);
    this.value = value;
    this.slowLogger = new SlowLogger(20);
    this.logger = new Logger();
  }

  /**
   * @param {Record<string, any>} state
   * @param {StepHandlerOptions} [stepHandlerOptions]
   */
  async handle(state, stepHandlerOptions) {
    if (this.stepOptions.skipWhen(state)) {
      console.log(`Skipping step: ${this.name}`);
      return;
    }
    super.handle(state, stepHandlerOptions);

    const output =
      typeof this.value === "function" ? this.value(state) : this.value;
    if (!output) {
      return;
    }
    const paddingTop = "\n";
    const paddingBottom = "\n";
    const logger =
      this.stepOptions?.slow && !stepHandlerOptions?.confirmAll
        ? this.slowLogger
        : this.logger;
    const message = paddingTop + output + paddingBottom;

    if (this.stepOptions?.header) {
      await this.logger.log(this.logger.box(message));
    } else {
      await logger.log(message, this.stepOptions?.preformatted);
    }
  }
}

/**
 * @typedef {{
 *   type: "confirm" | "input" | "multi-select" | "select",
 *   choices: (string | { name: string, value: string })[] }
 *   } ScenarioInputOptions
 */

/**
 * @extends {Step<ScenarioInputOptions>}
 */
export class ScenarioInput extends Step {
  /**
   * @param {string} name
   * @param {string | (c: Record<string, any>) => string | false } prompt
   * @param {ScenarioInputOptions} [scenarioInputOptions]
   */
  constructor(name, prompt, scenarioInputOptions) {
    super(name, scenarioInputOptions);
    this.prompt = prompt;
    this.prompter = new Prompter();
  }

  /**
   * @param {Record<string, any>} state
   * @param {StepHandlerOptions} [stepHandlerOptions]
   */
  async handle(state, stepHandlerOptions) {
    if (this.stepOptions.skipWhen(state)) {
      console.log(`Skipping step: ${this.name}`);
      return;
    }
    super.handle(state, stepHandlerOptions);
    const message =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;
    if (!message) {
      return;
    }

    const choices =
      this.stepOptions?.choices &&
      typeof this.stepOptions?.choices[0] === "string"
        ? this.stepOptions?.choices.map((s) => ({ name: s, value: s }))
        : this.stepOptions?.choices;

    if (this.stepOptions?.type === "multi-select") {
      state[this.name] = await this.prompter.checkbox({
        message,
        choices,
      });
    } else if (this.stepOptions?.type === "select") {
      state[this.name] = await this.prompter.select({
        message,
        choices,
      });
    } else if (this.stepOptions?.type === "input") {
      state[this.name] = await this.prompter.input({ message });
    } else if (this.stepOptions?.type === "confirm") {
      if (stepHandlerOptions?.confirmAll) {
        state[this.name] = true;
        return true;
      }

      state[this.name] = await this.prompter.confirm({
        message,
      });
    } else {
      throw new Error(
        `Error handling ScenarioInput, ${this.stepOptions?.type} is not supported.`,
      );
    }

    return state[this.name];
  }
}

/**
 * @typedef {{ whileConfig: { inputEquals: any, input: ScenarioInput, output: ScenarioOutput }}
 *   } ScenarioActionOptions
 */

/**
 * @extends {Step<ScenarioActionOptions>}
 */
export class ScenarioAction extends Step {
  /**
   * @param {string} name
   * @param {(state: Record<string, any>, options) => Promise<void>} action
   * @param {ScenarioActionOptions} [scenarioActionOptions]
   */
  constructor(name, action, scenarioActionOptions) {
    super(name, scenarioActionOptions);
    this.action = action;
  }

  /**
   * @param {Record<string, any>} state
   * @param {StepHandlerOptions} [stepHandlerOptions]
   */
  async handle(state, stepHandlerOptions) {
    if (this.stepOptions.skipWhen(state)) {
      console.log(`Skipping step: ${this.name}`);
      return;
    }
    const _handle = async () => {
      super.handle(state, stepHandlerOptions);
      await this.action(state, stepHandlerOptions);
    };

    if (!stepHandlerOptions?.confirmAll && this.stepOptions?.whileConfig) {
      const whileFn = () =>
        this.stepOptions.whileConfig.input.handle(state, stepHandlerOptions);

      let actual = await whileFn();
      let expected = this.stepOptions.whileConfig.inputEquals;

      while (actual === expected) {
        await _handle();
        await this.stepOptions.whileConfig.output.handle(
          state,
          stepHandlerOptions,
        );
        actual = await whileFn();
      }
    } else {
      await _handle();
    }
  }
}

export class Scenario {
  /**
   * @type {Record<string, any>}
   */
  state = {};

  /**
   * @type {(ScenarioOutput | ScenarioInput | ScenarioAction | Scenario)[]}
   */
  stepsOrScenarios = [];

  /**
   * @param {string} name
   * @param {(ScenarioOutput | ScenarioInput | ScenarioAction | null)[]} stepsOrScenarios
   * @param {Record<string, any>} initialState
   */
  constructor(name, stepsOrScenarios = [], initialState = {}) {
    this.name = name;
    this.stepsOrScenarios = stepsOrScenarios.filter((s) => !!s);
    this.state = { ...initialState, name };
  }

  /**
   * @param {StepHandlerOptions} stepHandlerOptions
   */
  async run(stepHandlerOptions) {
    for (const stepOrScenario of this.stepsOrScenarios) {
      if (stepOrScenario instanceof Scenario) {
        await stepOrScenario.run(stepHandlerOptions);
      } else {
        await stepOrScenario.handle(this.state, stepHandlerOptions);
      }
    }
  }
}
