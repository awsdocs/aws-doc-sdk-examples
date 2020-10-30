const mockSendEmail = jest.fn();
jest.mock("@aws-sdk/client-pinpoint/commands/SendMessagesCommand", () => ({
  Pinpoint: function Pinpoint() {
    this.SendMessagesCommand = mockSendEmail;
  },
}));
const { run } = require("../../pinpoint/src/pinpoint_send_email_message");

//test function
test("has to mock pinpoint#sendEmail", async (done) => {
  await run();
  expect(mockSendEmail).toHaveBeenCalled;
  done();
});
