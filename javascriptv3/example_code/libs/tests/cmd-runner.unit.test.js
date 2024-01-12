// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { it, describe } from "vitest";
import { getCommands } from "../cmd-runner";
import { testEqual } from "../utils/util-test";

describe("cmd-runner", () => {
  describe("getCommands", () => {
    it(
      "should split commands by spaces",
      testEqual(["do", "thing"], getCommands("do thing")),
    );

    it(
      "should trim spaces off the ends of commands",
      testEqual(["do", "thing"], getCommands("   do thing   ")),
    );
  });
});
