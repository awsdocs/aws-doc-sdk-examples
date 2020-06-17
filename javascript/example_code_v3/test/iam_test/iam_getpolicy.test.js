process.argv.push('--arg1', 'us-west-2');
const mockGetPolicy = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/GetPolicyCommand', () => ({
    IAM: function IAM() {
        this.GetPolicyCommand = mockGetPolicy
    }
}));
const {params, run} = require("../../iam/iam_getpolicy.js");

//test function
test("has to mock iam#getpolicy",  async (done) => {
    await run();
    expect(mockGetPolicy).toHaveBeenCalled;
    done();
});
