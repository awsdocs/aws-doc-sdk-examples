// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/delete-route-calculator.js";
import data from "../actions/inputs.json";

describe("test delete-route-calculator", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        CalculatorName: `${data.inputs.calculatorName}`,
      });
    },
    { timeout: 600000 },
  );
});
