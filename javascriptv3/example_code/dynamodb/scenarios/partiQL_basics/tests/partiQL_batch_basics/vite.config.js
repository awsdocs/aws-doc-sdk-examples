/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    restoreMocks: true,
    cache: false,
    testTimeout: 50000,
    coverage: {
      reporter: ['text', 'html']
    }
  },
});
