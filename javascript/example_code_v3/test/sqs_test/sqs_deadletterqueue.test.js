process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'SQS_QUEUE_NAME');

const mockSetQueueAttributes = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/SetQueueAttributesCommand', () => ({
    SQS: function SQS() {
        this.SetQueueAttributesCommand = mockSetQueueAttributes
    }
}));
const {run} = require("../../sqs/sqs_deadletterqueue.js");

//test function
test("has to mock SQS#deadletterqueue",  async (done) => {
    await run();
    expect(mockSetQueueAttributes).toHaveBeenCalled;
    done();
});
