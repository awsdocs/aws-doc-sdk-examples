process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'SQS_QUEUE_NAME');

const mockCreateQueue = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/CreateQueueCommand', () => ({
    SQS: function SQS() {
        this.CreateQueueCommand = mockCreateQueue
    }
}));
const {run} = require("../../sqs/sqs_changingvisibility.js");

//test function
test("has to mock SQS#changingvisibility",  async (done) => {
    await run();
    expect(mockCreateQueue).toHaveBeenCalled;
    done();
});
