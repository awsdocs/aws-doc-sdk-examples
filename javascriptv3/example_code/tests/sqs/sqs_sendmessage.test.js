const mockSendMessage = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/SendMessageCommand", () => ({
  SQS: function SQS() {
    this.SendMessageCommand = mockSendMessage;
  },
}));
const { run } = require("../../sqs/src/sqs_sendmessage.js");

test("has to mock SQS#sendmessage", async (done) => {
  await run();
  expect(mockSendMessage).toHaveBeenCalled;
  done();
});
