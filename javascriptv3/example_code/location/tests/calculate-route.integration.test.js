// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/calculate-route.js";
import data from "../actions/inputs.json";

describe("test calculate-route", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        CalculatorName: `${data.inputs.calculatorName}`,
        DeparturePosition: [-122.3321, 47.6062],
        DestinationPosition: [-123.1216, 49.2827],
        TravelMode: "Car",
        DistanceUnit: "Kilometers",
      });
    },
    { timeout: 600000 },
  );
});
