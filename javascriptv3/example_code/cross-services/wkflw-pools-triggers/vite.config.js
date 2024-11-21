// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    testTimeout: 3600000, // 1 hour timeout
    hookTimeout: 3600000, // 1 hour timeout
    sequence: {
      concurrent: false, // Run tests sequentially
    },
  },
});
