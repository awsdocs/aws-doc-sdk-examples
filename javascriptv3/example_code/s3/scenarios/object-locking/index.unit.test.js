// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vi } from "vitest";

import * as Scenario from "@aws-doc-sdk-examples/lib/scenario/index.js";

vi.doMock("fs/promises", () => ({
  default: {
    readFile: () => Promise.resolve(""),
    writeFile: () => Promise.resolve(),
  },
}));

const { getWorkflowStages } = await import("./index.js");

describe.skip("S3 Object Locking Workflow", () => {
  /**
   * @param {{}} state
   */
  const mockHandle = () => Promise.resolve();

  const initialState = {
    welcomeContinue: true,
    confirmCreateBuckets: true,
    confirmUpdateRetention: true,
    confirmPopulateBuckets: true,
  };

  const mockScenarios = {
    ...Scenario,
    ScenarioOutput: class ScenarioOutput {
      handle() {
        return mockHandle();
      }
    },
    ScenarioInput: class ScenarioInput {
      handle() {
        return mockHandle();
      }
    },
    ScenarioAction: class ScenarioAction {
      /**
       * @param {string} name
       * @param {Function} fn
       */
      constructor(name, fn) {
        if (name.startsWith("exitOn")) {
          this.handle = (state) => fn(state);
        } else {
          this.handle = () => mockHandle();
        }
      }
    },
  };

  it("should exit if welcomeContinue step resolves to false", async () => {
    const stages = getWorkflowStages(
      {
        ...mockScenarios,
        ScenarioInput: class ScenarioInput {
          constructor(name) {
            this.name = name;
          }

          /**
           * @param {{}} state
           */
          handle(state) {
            if (this.name === "welcomeContinue") {
              state.welcomeContinue = false;
              return Promise.resolve(false);
            }
            return Promise.resolve(true);
          }
        },
      },
      initialState,
    );

    const spy = vi.spyOn(process, "exit").mockImplementation(vi.fn());

    await stages.deploy.run({ verbose: true });

    expect(spy).toHaveBeenCalledWith(0);
  });

  it("should exit if confirmCreateBuckets step resolves to false", async () => {
    const stages = getWorkflowStages(
      {
        ...mockScenarios,
        ScenarioInput: class ScenarioInput {
          constructor(name) {
            this.name = name;
          }

          /**
           * @param {{}} state
           */
          handle(state) {
            if (this.name === "confirmCreateBuckets") {
              state.confirmCreateBuckets = false;
              return Promise.resolve(false);
            }
            return Promise.resolve(true);
          }
        },
      },
      initialState,
    );

    const spy = vi.spyOn(process, "exit").mockImplementationOnce(vi.fn());

    await stages.deploy.run({ verbose: true });

    expect(spy).toHaveBeenCalledWith(0);
  });

  it("should exit if confirmUpdateRetention step resolves to false", async () => {
    const stages = getWorkflowStages({
      ...mockScenarios,
      ScenarioInput: class ScenarioInput {
        constructor(name) {
          this.name = name;
        }

        /**
         * @param {{}} state
         */
        handle(state) {
          if (this.name === "confirmUpdateRetention") {
            state.confirmUpdateRetention = false;
            return Promise.resolve(false);
          }
          return Promise.resolve(true);
        }
      },
    });

    const spy = vi.spyOn(process, "exit").mockImplementationOnce(vi.fn());

    await stages.deploy.run({ verbose: true });

    expect(spy).toHaveBeenCalledWith(0);
  });

  it("should exit if confirmPopulateBuckets step resolves to false", async () => {
    const stages = getWorkflowStages(
      {
        ...mockScenarios,
        ScenarioInput: class ScenarioInput {
          constructor(name) {
            this.name = name;
          }

          /**
           * @param {{}} state
           */
          handle(state) {
            if (this.name === "confirmPopulateBuckets") {
              state.confirmPopulateBuckets = false;
              return Promise.resolve(false);
            }
            return Promise.resolve(true);
          }
        },
      },
      initialState,
    );

    const spy = vi.spyOn(process, "exit").mockImplementationOnce(vi.fn());

    await stages.deploy.run({ verbose: true });

    expect(spy).toHaveBeenCalledWith(0);
  });

  it("should exit if confirmUpdateLockPolicy step resolves to false", async () => {
    const stages = getWorkflowStages(
      {
        ...mockScenarios,
        ScenarioInput: class ScenarioInput {
          constructor(name) {
            this.name = name;
          }

          /**
           * @param {{}} state
           */
          handle(state) {
            if (this.name === "confirmUpdateLockPolicy") {
              state.confirmUpdateLockPolicy = false;
              return Promise.resolve(false);
            }
            return Promise.resolve(true);
          }
        },
      },
      initialState,
    );

    const spy = vi.spyOn(process, "exit").mockImplementationOnce(vi.fn());

    await stages.deploy.run({ verbose: true });

    expect(spy).toHaveBeenCalledWith(0);
  });

  it("should have the correct step order in the deploy scenario", () => {
    const stages = getWorkflowStages(Scenario);
    const deploySteps = stages.deploy.stepsOrScenarios;

    const expectedSteps = [
      "welcome",
      "welcomeContinue",
      "exitOnwelcomeContinueFalse",
      "createBuckets",
      "confirmCreateBuckets",
      "exitOnconfirmCreateBucketsFalse",
      "createBucketsAction",
      "updateRetention",
      "confirmUpdateRetention",
      "exitOnconfirmUpdateRetentionFalse",
      "updateRetentionAction",
      "populateBuckets",
      "confirmPopulateBuckets",
      "exitOnconfirmPopulateBucketsFalse",
      "populateBucketsAction",
      "updateLockPolicy",
      "confirmUpdateLockPolicy",
      "exitOnconfirmUpdateLockPolicyFalse",
      "updateLockPolicyAction",
      "confirmSetLegalHoldFileEnabled",
      "setLegalHoldFileEnabledAction",
      "confirmSetRetentionPeriodFileEnabled",
      "setRetentionPeriodFileEnabledAction",
      "confirmSetLegalHoldFileRetention",
      "setLegalHoldFileRetentionAction",
      "confirmSetRetentionPeriodFileRetention",
      "setRetentionPeriodFileRetentionAction",
      "saveState",
    ];

    const actualSteps = deploySteps.map((step) => step.name);

    expect(actualSteps).toEqual(expectedSteps);
  });
});
