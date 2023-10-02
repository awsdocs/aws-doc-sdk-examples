import { describe, it, expect } from "vitest";

import { publish as publishSms } from "../actions/publish-sms.js";
import { publish as publishTopic } from "../actions/publish-topic.js";

describe("publishSms", () => {
  it("should return MessageId property that is a string", async () => {
    const smsResponse = await publishSms();
    expect(typeof smsResponse.MessageId).toBe("string");
  });
});

describe("publishTopic", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await publishTopic();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
