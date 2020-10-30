const mockSetQueueAttributesLP = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/SetQueueAttributesCommand", () => ({
  SQS: function SQS() {
    this.SetQueueAttributesCommand = mockSetQueueAttributesLP;
  },
}));
const { run } = require("../../sqs/src/sqs_longpolling_existingqueue.js");

//test function
test("has to mock SQS#sqs_longpolling_existingqueue", async (done) => {
  await run();
  expect(mockSetQueueAttributesLP).toHaveBeenCalled;
  done();
});
