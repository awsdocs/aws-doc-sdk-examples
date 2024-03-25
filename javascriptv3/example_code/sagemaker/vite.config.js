// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { defineConfig } from "vitest/config";

const ONE_SECOND = 1000;
const ONE_MINUTE = ONE_SECOND * 60;
const ONE_HOUR = ONE_MINUTE * 60;

export default defineConfig({
  test: {
    testTimeout: ONE_HOUR * 2,
    hookTimeout: ONE_MINUTE * 30,
    threads: false,
  },
});
