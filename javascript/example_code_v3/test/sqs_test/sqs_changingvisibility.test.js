process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'ACCOUNT_ID');
process.argv.push('--arg3', 'QUEUE_NAME');

const mockChangingVisibility = jest.fn();
jest.mock('@aws-sdk/client-sqs/commands/ReceiveMessageCommand', () => ({
    SQS: function SQS() {
        this.ReceiveMessageCommand = mockChangingVisibility
    }
}));
const {run} = require("../../sqs/sqs_changingvisibility.js");

//test function
test("has to mock SQS#sqs_changingvisibility",  async (done) => {
    await run();
    expect(mockChangingVisibility).toHaveBeenCalled;
    done();
});
