process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'TOPIC_ARN');
process.argv.push('--arg2', 'MOBILE_ENDPOINT_ARN');

const mockSubscribe = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/SubscribeCommand', () => ({
    SNS: function SNS() {
        this.SubscribeCommand = mockSubscribe
    }
}));
const {run} = require("../../sns/sns_subscribeapp.js");

//test function
test("has to mock SNS#subscribeapp",  async (done) => {
    await run();
    expect(mockSubscribe).toHaveBeenCalled;
    done();
});
