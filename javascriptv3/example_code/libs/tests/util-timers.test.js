import { describe, it, expect, beforeAll, afterAll, jest } from "@jest/globals";
import { flushPromises } from "../utils/util-test";
import { retry, wait } from "../utils/util-timers";

describe("util-timers", () => {
  describe("retry", () => {
    beforeAll(() => {
      jest.useFakeTimers();
    });

    afterAll(() => {
      jest.useRealTimers();
    });

    it("should return instantly if the function resolves immediately", async () => {
      const result = await retry(
        { intervalInMs: 1, maxRetries: 1 },
        async () => true
      );
      expect(result).toBe(true);
    });

    it("should not return instantly if the function takes time to resolve", async () => {
      expect.assertions(2);
      let done = false;

      retry({ intervalInMs: 1, maxRetries: 1 }, () => wait(5))
        .then(() => {
          done = true;
        })
        .catch(console.error);

      expect(done).toBe(false);
      jest.advanceTimersByTime(5000);
      await flushPromises();
      expect(done).toBe(true);
    });

    it("should retry a failing promise maxRetries times before erroring out", async () => {
      expect.assertions(1);
      const fn = jest.fn(async () => {
        throw new Error();
      });

      retry({ intervalInMs: 1, maxRetries: 2 }, fn).catch(() => {
        expect(fn).toHaveBeenCalledTimes(3);
      });

      jest.runAllTimers();
      await flushPromises();
      jest.runAllTimers();
      await flushPromises();
      jest.runAllTimers();
      await flushPromises();
    });
  });
});
