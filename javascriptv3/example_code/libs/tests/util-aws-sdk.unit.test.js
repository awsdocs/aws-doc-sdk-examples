/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, expect, it } from "@jest/globals";
import { LambdaClient } from "@aws-sdk/client-lambda";
import {
  createClientForDefaultRegion,
  orDefaultRegion,
  DEFAULT_REGION,
  createClientForRegion,
} from "../utils/util-aws-sdk.js";
import { testEqual } from "../utils/util-test.js";

describe("service-aws-sdk", () => {
  describe("createClientForRegion", () => {
    it("should return a client with provided Region", async () => {
      const client = createClientForRegion("us-west-1", LambdaClient);
      const region = await client.config.region();
      expect(region).toEqual("us-west-1");
    });
  });

  describe("createClientForDefaultRegion", () => {
    it("should return a client with a default Region", async () => {
      const client = createClientForDefaultRegion(LambdaClient);
      const region = await client.config.region();
      expect(region).toEqual(DEFAULT_REGION);
    });
  });

  describe("orDefaultRegion", () => {
    it(
      "should return 'us-east-1' if value is undefined",
      testEqual("us-east-1", orDefaultRegion(undefined))
    );

    it(
      "should return the provided value",
      testEqual("us-west-1", orDefaultRegion("us-west-1"))
    );
  });
});
