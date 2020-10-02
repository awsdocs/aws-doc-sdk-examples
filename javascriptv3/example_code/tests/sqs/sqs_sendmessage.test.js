const mockSendMessage = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/SendMessageCommand", () => ({
  SQS: function SQS() {
    this.SendMessageCommand = mockSendMessage;
  },
}));
const { run } = require("../../sqs/sqs_sendmessage.js");

//test function
test("has to mock SQS#sendmessage", async (done) => {
  await run();
  expect(mockSendMessage).toHaveBeenCalled;
  done();
});
