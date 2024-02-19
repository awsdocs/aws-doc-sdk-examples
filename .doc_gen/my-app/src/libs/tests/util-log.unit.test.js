/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { it, describe } from "vitest";
import { log } from "../utils/util-log";
import { testEqual } from "../utils/util-test";

describe("log", () => {
  it("should parse a string from an object", testEqual("{}", log({})));
});
