const mockSendSMS = jest.fn();
jest.mock("@aws-sdk/client-pinpoint/commands/SendMessagesCommand", () => ({
  Pinpoint: function Pinpoint() {
    this.SendMessagesCommand = mockSendSMS;
  },
}));
const { run } = require("../../pinpoint/src/pinpoint_send_sms_message");

//test function
test("has to mock pinpoint#sendSms", async (done) => {
  await run();
  expect(mockSendSMS).toHaveBeenCalled;
  done();
});
