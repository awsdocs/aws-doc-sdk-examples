// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { defineConfig } from "vitest/config";

const timeout = 3600000; // 1 hour timeout

export default defineConfig({
  test: {
    testTimeout: timeout,
    hookTimeout: timeout,
    sequence: {
      concurrent: false, // Run tests sequentially
    },
  },
});
