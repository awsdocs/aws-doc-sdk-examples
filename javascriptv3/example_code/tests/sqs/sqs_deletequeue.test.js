const mockDeleteQueue = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/DeleteQueueCommand", () => ({
  SQS: function SQS() {
    this.DeleteQueueCommand = mockDeleteQueue;
  },
}));
const { run } = require("../../sqs/src/sqs_deletequeue.js");

//test function
test("has to mock SQS#deletequeue", async (done) => {
  await run();
  expect(mockDeleteQueue).toHaveBeenCalled;
  done();
});
