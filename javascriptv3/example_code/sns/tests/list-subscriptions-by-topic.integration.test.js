import { describe, it, expect } from "vitest";
import { listSubscriptionsByTopic } from "../actions/list-subscriptions-by-topic.js";

describe("listSubscriptionsByTopic", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await listSubscriptionsByTopic();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
