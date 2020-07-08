const mockCreateQueue = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/CreateQueueCommand", () => ({
  SQS: function SQS() {
    this.CreateQueueCommand = mockCreateQueue;
  },
}));
const { run } = require("../../sqs/sqs_changingvisibility.js");

//test function
test("has to mock SQS#changingvisibility", async (done) => {
  await run();
  expect(mockCreateQueue).toHaveBeenCalled;
  done();
});
