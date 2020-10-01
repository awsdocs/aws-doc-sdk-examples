const mockChangingVisibility = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/ReceiveMessageCommand", () => ({
  SQS: function SQS() {
    this.ReceiveMessageCommand = mockChangingVisibility;
  },
}));
const { run } = require("../../sqs/sqs_changingvisibility.js");

//test function
test("has to mock SQS#sqs_changingvisibility", async (done) => {
  await run();
  expect(mockChangingVisibility).toHaveBeenCalled;
  done();
});
