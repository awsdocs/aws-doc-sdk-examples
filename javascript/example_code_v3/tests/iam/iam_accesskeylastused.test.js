process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'ACCESS_KEY_ID');
const mockGetAccessKeyLastUsed = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/GetAccessKeyLastUsedCommand', () => ({
    IAM: function IAM() {
        this.GetAccessKeyLastUsedCommand = mockGetAccessKeyLastUsed
    }
}));
const {params, run} = require("../../iam/iam_accesskeylastused.js");

//test function
test("has to mock iam#getAccessKeyLastUsed",  async (done) => {
    await run();
    expect(mockGetAccessKeyLastUsed).toHaveBeenCalled;
    done();
});
