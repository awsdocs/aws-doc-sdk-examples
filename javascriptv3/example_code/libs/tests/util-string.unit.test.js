// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { it, describe, expect } from "vitest";
import { getUniqueName, postfix } from "../utils/util-string";

describe("util-string", () => {
  describe("getUniqueName", () => {
    it("should return a string", () => {
      const name = getUniqueName("Hello");
      expect(typeof name).toBe("string");
    });

    it("should return a unique name even when passed the same value", () => {
      const value = "Hello";
      const u1 = getUniqueName(value);
      const u2 = getUniqueName(value);
      expect(u1).not.toEqual(u2);
    });

    it("should throw an error if a falsy value is passed in for the prefix", () => {
      expect(() => getUniqueName()).toThrowError();
      expect(() => getUniqueName("")).toThrowError();
      expect(() => getUniqueName(0)).toThrowError();
    });
  });

  describe("postfix", () => {
    it("should add the provided string to the end of the source string", () => {
      expect(postfix("Hello ", "World")).toEqual("Hello World");
    });

    it("should throw an error when given a non-string as the value to affix", () => {
      expect(() => postfix("Hello ", 123)).toThrow();
    });
  });
});
