const mockSetQueueAttributes = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/SetQueueAttributesCommand", () => ({
  SQS: function SQS() {
    this.SetQueueAttributesCommand = mockSetQueueAttributes;
  },
}));
const { run } = require("../../sqs/src/sqs_deadletterqueue.js");

test("has to mock SQS#deadletterqueue", async (done) => {
  await run();
  expect(mockSetQueueAttributes).toHaveBeenCalled;
  done();
});
