const {
  handler,
} = require("../scenarios/lambda-triggers/functions/confirmation-post.js");
const { sendMock } = require("../__mocks__/@aws-sdk/client-ses.js");

describe("confirmation-post", () => {
  it("should attempt to send an email if the even has an email address", async () => {
    await handler({
      request: { userAttributes: { email: "mail@example.com" } },
    });

    expect(sendMock).toHaveBeenCalled();
  });
});
