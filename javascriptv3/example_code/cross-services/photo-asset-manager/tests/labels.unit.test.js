// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";

import { getHandler, labelsReducer } from "../src/functions/labels.js";
import { RESPONSE_HEADERS } from "../src/common.js";

describe("labels handler", () => {
  it("should return the output from getLabels as a JSON encoded string", async () => {
    const getLabels = () =>
      Promise.resolve({
        one: 1,
        two: 2,
        three: 3,
      });

    const handler = getHandler({ getLabels });

    await expect(handler()).resolves.toEqual({
      headers: RESPONSE_HEADERS,
      statusCode: 200,
      body: JSON.stringify({
        labels: {
          one: 1,
          two: 2,
          three: 3,
        },
      }),
    });
  });

  describe("labels reducer", () => {
    it("should take label records and return an object with labels as keys and counts as values", () => {
      const records = [
        { Label: "one", Count: 1 },
        { Label: "two", Count: 2 },
        { Label: "three", Count: 3 },
      ];

      const expected = {
        one: { count: 1 },
        two: { count: 2 },
        three: { count: 3 },
      };

      const actual = records.reduce(labelsReducer, {});

      expect(actual).toEqual(expected);
    });
  });
});
