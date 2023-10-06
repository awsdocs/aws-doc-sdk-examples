import { describe, it, expect } from "vitest";
import { getTopicAttributes } from "../actions/get-topic-attributes.js";

describe("getTopicAttributes", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await getTopicAttributes();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
