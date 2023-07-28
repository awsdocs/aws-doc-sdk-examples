import { describe, it, expect } from "vitest";
import { listTopics } from "../actions/list-topics.js";

describe("listTopics", () => {
  it("should return a Topics property that is an array", async () => {
    const response = await listTopics();
    expect(response.Topics).toBeInstanceOf(Array);
  });
});
