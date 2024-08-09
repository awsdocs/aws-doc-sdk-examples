// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

const confirm = vi.fn();
const prompter = {
  Prompter: function () {
    return { confirm };
  },
};
vi.doMock("../prompter", () => prompter);

const { confirmOrExit } = await import("../scenario/steps-common.js");
const scenarios = await import("../scenario/scenario.js");

describe("steps-common", () => {
  describe("confirmOrExit", () => {
    it("should call the Prompter's confirm method", async () => {
      confirm.mockResolvedValueOnce(true);

      const scenario = new scenarios.Scenario("testConfirmOrExit", [
        confirmOrExit(scenarios),
      ]);
      await scenario.run();

      expect(confirm).toHaveBeenCalledWith({ message: "Continue? (true)" });
    });

    it("should continue if the user confirms", async () => {
      const processSpy = vi
        .spyOn(process, "exit")
        .mockImplementationOnce(vi.fn());
      confirm.mockResolvedValueOnce(true);

      const scenario = new scenarios.Scenario("testConfirmOrExit", [
        confirmOrExit(scenarios),
      ]);
      await scenario.run();

      expect(processSpy).not.toHaveBeenCalled();
    });

    it("should exit if the user does not confirm", async () => {
      const processSpy = vi
        .spyOn(process, "exit")
        .mockImplementationOnce(vi.fn());
      confirm.mockResolvedValueOnce(false);

      const scenario = new scenarios.Scenario("testConfirmOrExit", [
        confirmOrExit(scenarios),
      ]);
      await scenario.run();

      expect(processSpy).toHaveBeenCalledWith(0);
    });
  });
});
