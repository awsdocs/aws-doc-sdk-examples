process.argv.push('--arg1', 'eu-west-1');

const mockListTopics = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/ListTopicsCommand', () => ({
    SNS: function SNS() {
        this.ListTopicsCommand = mockListTopics
    }
}));
const {run} = require("../../sns/sns_listtopics.js");

//test function
test("has to mock SNS#listtopics",  async (done) => {
    await run();
    expect(mockListTopics).toHaveBeenCalled;
    done();
});
