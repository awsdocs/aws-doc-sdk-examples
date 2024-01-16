// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vi } from "vitest";
import { retry } from "../utils/util-timers";

describe("util-timers", () => {
  describe("retry", () => {
    it("should only call a function once if that function succeeds", async () => {
      const fn = vi.fn(() => Promise.resolve());
      await retry({ intervalInMs: 1000, maxRetries: 3 }, fn);
      expect(fn).toHaveBeenCalledTimes(1);
    });

    it("should call a function multiple times if that function fails", async () => {
      const fn = vi.fn(() => Promise.reject(new Error("Fail")));
      await retry({ intervalInMs: 1, maxRetries: 3 }, fn).catch(() =>
        Promise.resolve(),
      );
      expect(fn).toHaveBeenCalledTimes(4);
    });

    it("should retry a function if it fails, but succeeds on the second try", async () => {
      const fn = vi.fn(() => {
        if (fn.mock.calls.length === 1) {
          return Promise.reject(new Error("Fail"));
        }
        return Promise.resolve(true);
      });
      await retry({ intervalInMs: 1, maxRetries: 3 }, fn);
      expect(fn).toHaveBeenCalledTimes(2);
    });
  });
});
