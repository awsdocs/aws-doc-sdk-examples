/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    testTimeout: 50000,
    hookTimeout: 50000,
    threads: false,
  },
});
