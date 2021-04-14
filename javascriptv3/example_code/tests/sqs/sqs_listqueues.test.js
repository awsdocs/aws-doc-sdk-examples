const mockListQueue = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/ListQueuesCommand", () => ({
  SQS: function SQS() {
    this.ListQueuesCommand = mockListQueue;
  },
}));
const { run } = require("../../sqs/src/sqs_listqueues.js");

test("has to mock SQS#listqueues", async (done) => {
  await run();
  expect(mockListQueue).toHaveBeenCalled;
  done();
});
