import { describe, it, expect, vi } from "vitest";
import { retry } from "../utils/util-timers";

describe("util-timers", () => {
  describe("retry", () => {
    it("should only call a function once if that function succeeds", async () => {
      const fn = vi.fn(async () => {});
      const retryEveryOneSecond = retry({ intervalInMs: 1000, maxRetries: 3 });
      await retryEveryOneSecond(fn);
      expect(fn).toHaveBeenCalledTimes(1);
    });

    it("should call a function multiple times if that function fails", async () => {
      const fn = vi.fn(async () => {
        throw new Error("Fail");
      });
      const retryEveryOneSecond = retry({ intervalInMs: 1, maxRetries: 3 });
      await retryEveryOneSecond(fn).catch(() => {});
      expect(fn).toHaveBeenCalledTimes(4);
    });

    it("should retry a function if it fails, but succeeds on the second try", async () => {
      const fn = vi.fn(async () => {
        if (fn.mock.calls.length === 1) {
          throw new Error("Fail");
        }
      });
      const retryEveryOneSecond = retry({ intervalInMs: 1, maxRetries: 3 });
      await retryEveryOneSecond(fn);
      expect(fn).toHaveBeenCalledTimes(2);
    });
  });
});
