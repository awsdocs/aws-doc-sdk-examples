import { jest } from "@jest/globals";

let mockSendFn = jest.fn(async () => {});

jest.unstable_mockModule("@aws-sdk/client-ses", () => {
  return {
    SES: class {
      send = mockSendFn;
    },
    SendEmailCommand: class {},
  };
});

describe("confirmation-post", () => {
  let handler;

  beforeAll(async () => {
    const mod = await import("../scenarios/lambda-triggers/functions/confirmation-post.mjs");
    handler = mod.handler;
  })

  it("should attempt to send an email if the even has an email address", async () => {
    await handler({
      request: { userAttributes: { email: "mail@example.com" } },
    });

    expect(mockSendFn).toHaveBeenCalled();
  });
});
