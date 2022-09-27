/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it } from "@jest/globals";
import { inc } from "ramda";
import {
  concatMap,
  flipApply,
  flipMap,
  parseString,
  promiseAll,
} from "../ext-ramda";
import { testEqual } from "../utils/util-test";

describe("ext-ramda", () => {
  describe("concatMap", () => {
    it(
      "should concatenate the provided value to each element in a list",
      testEqual(["a1", "a2", "a3"], concatMap("a")(["1", "2", "3"]))
    );
  });

  describe("flipApply", () => {
    it(
      "should apply a function to a list of args, but take the args first",
      testEqual(
        2,
        flipApply([1, 1], (a, b) => a + b)
      )
    );
  });

  describe("flipMap", () => {
    it(
      "should apply a function to each element in a list, but it should take the list first",
      testEqual([2, 2], flipMap([1, 1], inc))
    );
  });

  describe("parseString", () => {
    it(
      "should not modify a string passed to it",
      testEqual("Hello, World!", parseString("Hello, World!"))
    );

    it(
      "should return the 'message' property of an error",
      testEqual("error message", parseString(new Error("error message")))
    );

    it("should stringify non-string values", testEqual("{}", parseString({})));
  });

  describe("promiseAll", () => {
    it(
      "should resolve to a list of all resolved promise results",
      testEqual(
        ["a", "b", "c"],
        promiseAll([
          Promise.resolve("a"),
          Promise.resolve("b"),
          Promise.resolve("c"),
        ])
      )
    );
  });
});
