


const mockCreateQueueLongPolling = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/CreateQueueCommand', () => ({
    SQS: function SQS() {
        this.CreateQueueCommand = mockCreateQueueLongPolling
    }
}));
const {run} = require("../../sqs/sqs_longpolling_createqueue.js");

//test function
test("has to mock SQS#longpolling_createqueue",  async (done) => {
    await run();
    expect(mockCreateQueueLongPolling).toHaveBeenCalled;
    done();
});
