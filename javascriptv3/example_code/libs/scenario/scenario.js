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
   * @param {O & StepOptions} [stepOptions]
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
        }<${this.name}>`,
      );
      console.log(
        `[DEBUG ${new Date().toISOString()}] State: ${JSON.stringify(state)}`,
      );
    }
  }
}

/**
 * @typedef {{ slow: boolean, header: boolean, preformatted: boolean }} ScenarioOutputOptions
 */

/**
 * @extends {Step<ScenarioOutputOptions>}
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
 *   choices: (string | { name: string, value: string })[],
 *   default: string | string[] | boolean }
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
  async handle(state, stepHandlerOptions = {}) {
    if (this.stepOptions.skipWhen(state)) {
      console.log(`Skipping step: ${this.name}`);
      return;
    }
    super.handle(state, stepHandlerOptions);

    if (stepHandlerOptions.confirmAll && this.stepOptions.default) {
      state[this.name] = this.stepOptions.default;
      return state[this.name];
    } else if (stepHandlerOptions.confirmAll && !this.stepOptions.default) {
      if (this.stepOptions?.type === "confirm") {
        state[this.name] = true;
        return true;
      }
      throw new Error(
        `Error handling ScenarioInput. confirmAll was selected for ${this.name} but no default was provided.`,
      );
    }

    const message = this._getPrompt(state);

    switch (this.stepOptions?.type) {
      case "multi-select":
        await this._handleMultiSelect(state, message);
        break;
      case "select":
        await this._handleSelect(state, message);
        break;
      case "input":
        await this._handleInput(state, message);
        break;
      case "confirm":
        await this._handleConfirm(state, message);
        break;
      default:
        throw new Error(
          `Error handling ScenarioInput, ${this.stepOptions?.type} is not supported.`,
        );
    }

    return state[this.name];
  }

  /**
   * @param {Record<string, any>} state
   */
  _getPrompt(state) {
    const prompt =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;
    const message =
      this.stepOptions.type !== "confirm" && this.stepOptions.default
        ? `${prompt} (${this.stepOptions.default})`
        : prompt;

    if (!message) {
      throw new Error(`Error handling ScenarioInput. Missing prompt.`);
    }

    return message;
  }

  _getChoices() {
    if (this.choices) {
      return this.choices;
    }

    this.choices =
      this.stepOptions?.choices &&
      typeof this.stepOptions?.choices[0] === "string"
        ? this.stepOptions?.choices.map((s) => ({ name: s, value: s }))
        : this.stepOptions?.choices;

    return this.choices;
  }

  /**
   * @param {Record<string, any>} state
   * @param {string} message
   */
  async _handleMultiSelect(state, message) {
    const result = await this.prompter.checkbox({
      message,
      choices: this._getChoices(),
    });

    if (!result.length && this.stepOptions.default) {
      state[this.name] = this.stepOptions.default;
    } else if (!result.length) {
      throw new Error(
        `Error handing ScenarioInput. Result of ${this.name} was empty.`,
      );
    } else {
      state[this.name] = result;
    }
  }

  /**
   * @param {Record<string, any>} state
   * @param {string} message
   */
  async _handleSelect(state, message) {
    if (this.stepOptions?.type === "select") {
      const result = await this.prompter.select({
        message,
        choices: this._getChoices(),
      });

      if (!result && this.stepOptions.default) {
        state[this.name] = this.stepOptions.default;
      } else if (!result) {
        throw new Error(
          `Error handing ScenarioInput. Result of ${this.name} was empty.`,
        );
      } else {
        state[this.name] = result;
      }
    }
  }

  /**
   * @param {Record<string, any>} state
   * @param {string} message
   */
  async _handleInput(state, message) {
    const result = await this.prompter.input({ message });

    if (!result && this.stepOptions.default) {
      state[this.name] = this.stepOptions.default;
    } else if (!result) {
      throw new Error(
        `Error handing ScenarioInput. Result of ${this.name} was empty.`,
      );
    } else {
      state[this.name] = result;
    }
  }

  /**
   * @param {Record<string, any>} state
   * @param {string} message
   */
  async _handleConfirm(state, message) {
    const result = await this.prompter.confirm({
      message,
    });

    state[this.name] = result;
  }
}

/**
 * @typedef {{ whileConfig: { whileFn: (state: Record<string, any>) => boolean, input: ScenarioInput, output: ScenarioOutput }}
 *   } ScenarioActionOptions
 */

/**
 * @extends {Step<ScenarioActionOptions>}
 */
export class ScenarioAction extends Step {
  /**
   * @param {string} name
   * @param {(state: Record<string, any>, options) => Promise<void>} action
   * @param {Step<ScenarioActionOptions>['stepOptions']} [scenarioActionOptions]
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
      const whileFn = this.stepOptions.whileConfig.whileFn;
      const output = this.stepOptions.whileConfig.output;
      const input = this.stepOptions.whileConfig.input;
      await input.handle(state, stepHandlerOptions);

      let runAction = whileFn(state);
      while (runAction) {
        await _handle();
        output &&
          (await this.stepOptions.whileConfig.output.handle(
            state,
            stepHandlerOptions,
          ));
        await input.handle(state, stepHandlerOptions);
        runAction = whileFn(state);
      }
    } else {
      await _handle();
    }
  }
}

export class Scenario {
  /**
   * @type { { earlyExit: boolean } & Record<string, any>}
   */
  state;

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
    this.state = { ...initialState, name, earlyExit: false };
  }

  /**
   * @param {StepHandlerOptions} stepHandlerOptions
   */
  async run(stepHandlerOptions) {
    for (const stepOrScenario of this.stepsOrScenarios) {
      /**
       * Add an escape hatch for actions that terminate the scenario early.
       */
      if (this.state.earlyExit) {
        return;
      }

      if (stepOrScenario instanceof Scenario) {
        await stepOrScenario.run(stepHandlerOptions);
      } else {
        await stepOrScenario.handle(this.state, stepHandlerOptions);
      }
    }
  }
}
