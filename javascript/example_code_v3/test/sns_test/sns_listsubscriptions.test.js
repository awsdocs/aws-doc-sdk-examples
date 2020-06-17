process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'TOPIC_ARN');

const mockListSubscriptionsByTopic = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/ListSubscriptionsByTopicCommand', () => ({
    SNS: function SNS() {
        this.ListSubscriptionsByTopicCommand = mockListSubscriptionsByTopic
    }
}));
const {run} = require("../../sns/sns_listsubscriptions.js");

//test function
test("has to mock SNS#listsubscriptions",  async (done) => {
    await run();
    expect(mockListSubscriptionsByTopic).toHaveBeenCalled;
    done();
});
