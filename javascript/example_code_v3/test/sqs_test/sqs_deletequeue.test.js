process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'SQS_QUEUE_NAME');

const mockDeleteQueue = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/DeleteQueueCommand', () => ({
    SQS: function SQS() {
        this.DeleteQueueCommand = mockDeleteQueue
    }
}));
const {run} = require("../../sqs/sqs_deletequeue.js");

//test function
test("has to mock SQS#deletequeue",  async (done) => {
    await run();
    expect(mockDeleteQueue).toHaveBeenCalled;
    done();
});
