// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { expect } from "vitest";

/**
 * Expects the provided string to be a non-empty string.
 *
 * @param {string} string - The string to be checked.
 */
export const expectToBeANonEmptyString = (string) => {
  expect(typeof string).toBe("string");
  expect(string.length).not.toBe(0);
};
