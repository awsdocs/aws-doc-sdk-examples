process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'ATTRIBUTE_NAME');

const mockGetSMEAttributes = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/GetSMSAttributesCommand', () => ({
    SNS: function SNS() {
        this.GetSMSAttributesCommand = mockGetSMEAttributes
    }
}));
const {run} = require("../../sns/sns_getsmstype.js");

//test function
test("has to mock SNS#getsmstype",  async (done) => {
    await run();
    expect(mockGetSMEAttributes).toHaveBeenCalled;
    done();
});
