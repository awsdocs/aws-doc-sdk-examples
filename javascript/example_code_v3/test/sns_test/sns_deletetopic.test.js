process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'TOPIC_ARN');

const mockDeleteTopic = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/DeleteTopicCommand', () => ({
    SNS: function SNS() {
        this.DeleteTopicCommand = mockDeleteTopic
    }
}));
const {run} = require("../../sns/sns_deletetopic.js");

//test function
test("has to mock SNS#deletetopic",  async (done) => {
    await run();
    expect(mockDeleteTopic).toHaveBeenCalled;
    done();
});
