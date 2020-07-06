process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'ATTRIBUTE_NAME');
process.argv.push('--arg1', 'TOPIC_ARN');
process.argv.push('--arg2', 'NEW_ATTRIBUTE_VALUE');

const mockSetTopicAttributes = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/SetTopicAttributesCommand', () => ({
    SNS: function SNS() {
        this.SetTopicAttributesCommand = mockSetTopicAttributes
    }
}));
const {run} = require("../../sns/sns_settopicattributes.js");

//test function
test("has to mock SNS#settopicattributes",  async (done) => {
    await run();
    expect(mockSetTopicAttributes).toHaveBeenCalled;
    done();
});
