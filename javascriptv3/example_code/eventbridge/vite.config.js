/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { defineConfig } from "vitest/config";

const ONE_SECOND = 1000;
const ONE_MINUTE = ONE_SECOND * 60;

export default defineConfig({
  test: {
    testTimeout: ONE_MINUTE * 45,
    hookTimeout: ONE_MINUTE * 5,
    threads: false,
  },
});
