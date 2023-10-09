/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";
import { getDatastore } from "../actions/get-datastore.js";

describe("getDatastore", () => {
  it("should throw an error with the default fake data store ID", async () => {
    try {
      await getDatastore();
    } catch (err) {
      console.log(err.message);
      expect(err.message).toEqual(
        "1 validation error detected: Value 'DATASTORE_ID' at 'datastoreId' failed to satisfy constraint: Member must satisfy regular expression pattern: [0-9a-z]{32}"
      );
    }
  });
});
