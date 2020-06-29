process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'EMAIL');
process.argv.push('--arg3', 'TOPIC_ARN');
process.argv.push('--arg4', 'EMAIL_ADDRESS');

const mockSubscribe = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/SubscribeCommand', () => ({
    SNS: function SNS() {
        this.SubscribeCommand = mockSubscribe
    }
}));
const {run} = require("../../sns/sns_subscribeemail.js");

//test function
test("has to mock SNS#subscribeemail",  async (done) => {
    await run();
    expect(mockSubscribe).toHaveBeenCalled;
    done();
});
