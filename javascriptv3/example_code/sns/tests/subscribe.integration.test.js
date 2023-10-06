import { describe, it, expect } from "vitest";
import { subscribeApp } from "../actions/subscribe-app.js";
import { subscribeEmail } from "../actions/subscribe-email.js";
import { subscribeLambda } from "../actions/subscribe-lambda.js";

describe("subscribeApp", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await subscribeApp();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});

describe("subscribeEmail", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await subscribeEmail();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});

describe("subscribeLambda", () => {
  it("should throw an error with the default fake topic ARN", async () => {
    try {
      await subscribeLambda();
    } catch (err) {
      expect(err.message).toEqual(
        "Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1",
      );
    }
  });
});
