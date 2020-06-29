process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'SQS_QUEUE_URL');

const mockSetQueueAttributesLP = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/SetQueueAttributesCommand', () => ({
    SQS: function SQS() {
        this.SetQueueAttributesCommand = mockSetQueueAttributesLP
    }
}));
const {run} = require("../../sqs/sqs_longpolling_existingqueue.js");

//test function
test("has to mock SQS#sqs_longpolling_existingqueue",  async (done) => {
    await run();
    expect(mockSetQueueAttributesLP).toHaveBeenCalled;
    done();
});
