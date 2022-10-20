/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { curry } from "ramda";
import { expect, jest } from "@jest/globals";

const flushPromises = () =>
  new Promise(jest.requireActual("timers").setImmediate);

const testEqual = curry(
  (expected, actual) => async () => expect(await actual).toEqual(expected)
);

const testThrows = (fn) => () => {
  expect(fn).toThrowError();
};

export { flushPromises, testEqual, testThrows };
