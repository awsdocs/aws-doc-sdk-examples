import { test, vi } from "vitest";
import { main } from "../index.js";

vi.mock("readline/promises", () => {
  return {
    createInterface: () => {
      return {
        question: vi.fn(() => Promise.resolve("y")),
        close: vi.fn(),
      };
    },
  };
});

test("getting started example should run", async () => {
  await main();
});
