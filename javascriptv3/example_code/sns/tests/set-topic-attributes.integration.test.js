import { describe, it, expect } from "vitest";

import { setTopicAttributes } from "../actions/set-topic-attributes.js";

describe("setTopicAttributes", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await setTopicAttributes();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
