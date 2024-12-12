// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { Prompter } from "../prompter.js";
import { Logger } from "../logger.js";

/**
 * @typedef {{ confirmAll: boolean, verbose: boolean, noArt: boolean }} StepHandlerOptions
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
 * @typedef {{ header: boolean, preformatted: boolean }} ScenarioOutputOptions
 */

/**
 * @extends {Step<ScenarioOutputOptions>}
 */
export class ScenarioOutput extends Step {
  /**
   * @param {string} name
   * @param {string | (state: Record<string, any>) => string | false} value
   * @param {Step<ScenarioOutputOptions>['stepOptions']} [scenarioOutputOptions]
   */
  constructor(name, value, scenarioOutputOptions = {}) {
    super(name, scenarioOutputOptions);
    this.value = value;
    this.logger = new Logger();
  }

  /**
   * @param {Record<string, any>} state
   * @param {StepHandlerOptions} [stepHandlerOptions]
   */
  async handle(state, stepHandlerOptions) {
    if (this.stepOptions.skipWhen(state)) {
      if (stepHandlerOptions.verbose) {
        console.log(`Skipping step: ${this.name}`);
      }
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
    const logger = this.logger;
    const message = paddingTop + output + paddingBottom;

    if (this.stepOptions?.header) {
      if (stepHandlerOptions.noArt === true) {
        await this.logger.log(message);
      } else {
        await this.logger.log(this.logger.box(message));
      }
    } else {
      await logger.log(message, this.stepOptions?.preformatted);
    }
  }
}

/**
 * @typedef {{
 *   type: "confirm" | "input" | "multi-select" | "select" | "password",
 *   choices: (string | { name: string, value: string })[] | () => (string | { name: string, value: string })[],
 *   default: string | string[] | boolean | () => string | string[] | boolean }
 *   } ScenarioInputOptions
 */

/**
 * @extends {Step<ScenarioInputOptions>}
 */
export class ScenarioInput extends Step {
  /**
   * @param {string} name
   * @param {string | (c: Record<string, any>) => string | false } prompt
   * @param {Step<ScenarioInputOptions>['stepOptions']} [scenarioInputOptions]
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
      if (stepHandlerOptions.verbose) {
        console.log(`Skipping step: ${this.name}`);
      }
      return;
    }
    super.handle(state, stepHandlerOptions);

    this.default =
      typeof this.stepOptions.default === "function"
        ? this.stepOptions.default(state)
        : this.stepOptions.default;

    if (
      stepHandlerOptions.confirmAll &&
      this.stepOptions.default !== undefined
    ) {
      state[this.name] = this.default;
      return state[this.name];
    }
    if (stepHandlerOptions.confirmAll) {
      if (this.stepOptions?.type === "confirm") {
        state[this.name] = true;
        return true;
      }
      throw new Error(
        `Error handling ScenarioInput. confirmAll was selected for ${this.name} but no default was provided.`,
      );
    }

    switch (this.stepOptions?.type) {
      case "multi-select":
        await this._handleMultiSelect(state);
        break;
      case "select":
        await this._handleSelect(state);
        break;
      case "input":
        await this._handleInput(state);
        break;
      case "confirm":
        await this._handleConfirm(state);
        break;
      case "password":
        await this._handlePassword(state);
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
  _getChoices(state) {
    if (this.choices) {
      return this.choices;
    }

    const rawChoices =
      typeof this.stepOptions.choices === "function"
        ? this.stepOptions.choices(state)
        : this.stepOptions.choices;

    if (!rawChoices) {
      throw new Error(
        `Error handling ScenarioInput. Could not get choices for ${this.name}.`,
      );
    }

    this.choices =
      typeof rawChoices[0] === "string"
        ? rawChoices.map((s) => ({ name: s, value: s }))
        : rawChoices;

    return this.choices;
  }

  /**
   * @param {Record<string, any>} state
   */
  async _handleMultiSelect(state) {
    const message =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;

    if (!message) {
      throw new Error("Error handling ScenarioInput. Missing prompt.");
    }

    const result = await this.prompter.checkbox({
      message,
      choices: this._getChoices(state),
    });

    if (!result.length && this.default) {
      state[this.name] = this.default;
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
   */
  async _handleSelect(state) {
    const message =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;

    if (!message) {
      throw new Error("Error handling ScenarioInput. Missing prompt.");
    }

    if (this.stepOptions?.type === "select") {
      const result = await this.prompter.select({
        message,
        choices: this._getChoices(state),
      });

      if (!result && this.default) {
        state[this.name] = this.default;
      } else if (result == null) {
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
   */
  async _handleInput(state) {
    const prompt =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;
    const message = this.default ? `${prompt} (${this.default})` : prompt;

    if (!message) {
      throw new Error("Error handling ScenarioInput. Missing prompt.");
    }

    const result = await this.prompter.input({ message });

    if (!result && this.default) {
      state[this.name] = this.default;
    } else if (result === undefined) {
      throw new Error(
        `Error handing ScenarioInput. Result of ${this.name} was empty.`,
      );
    } else {
      state[this.name] = result;
    }
  }

  /**
   * @param {Record<string, any>} state
   */
  async _handleConfirm(state) {
    const message =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;

    if (!message) {
      throw new Error("Error handling ScenarioInput. Missing prompt.");
    }

    const result = await this.prompter.confirm({
      message,
    });

    state[this.name] = result;
  }

  /**
   * @param {*} state
   */
  async _handlePassword(state) {
    const message =
      typeof this.prompt === "function" ? this.prompt(state) : this.prompt;

    if (!message) {
      throw new Error("Error handling ScenarioInput. Missing prompt.");
    }

    const result = await this.prompter.password({
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
      if (stepHandlerOptions.verbose) {
        console.log(`Skipping step: ${this.name}`);
      }
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
