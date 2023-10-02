import { describe, it, expect } from "vitest";
import { confirmSubscription } from "../actions/confirm-subscription.js";

describe("confirmSubscription", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await confirmSubscription();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
