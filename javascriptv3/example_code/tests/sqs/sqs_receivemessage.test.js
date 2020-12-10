const mockReceiveMessages = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/ReceiveMessageCommand", () => ({
  SQS: function SQS() {
    this.ReceiveMessageCommand = mockReceiveMessages;
  },
}));
const { run } = require("../../sqs/src/sqs_receivemessage.js");

test("has to mock SQS#receivemessage", async (done) => {
  await run();
  expect(mockReceiveMessages).toHaveBeenCalled;
  done();
});
