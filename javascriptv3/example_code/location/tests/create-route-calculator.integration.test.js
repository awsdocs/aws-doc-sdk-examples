// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/create-route-calculator.js";
import data from "../actions/inputs.json";

describe("test create-route-calculator", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        CalculatorName: `${data.inputs.calculatorName}`,
        DataSource: "Esri",
      });
    },
    { timeout: 600000 },
  );
});
