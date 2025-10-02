// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { invokeModel } from "../models/amazonNovaCanvas/invokeModel.js";
import { expectToBeANonEmptyString } from "./test_tools.js";

describe("Invoking Amazon Nova Canvas", () => {
  it(
    "should return a response",
    async () => {
      const response = await invokeModel();
      expectToBeANonEmptyString(response);
    },
    { timeout: 600000 },
  );
});
