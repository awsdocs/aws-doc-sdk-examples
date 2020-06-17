process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'IAM_USER_NAME');
const mockCreateAccessKey = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/CreateAccessKeyCommand', () => ({
    IAM: function IAM() {
        this.CreateAccessKeyCommand = mockCreateAccessKey
    }
}));
const {params, run} = require("../../iam/iam_createaccesskeys.js");

//test function
test("has to mock iam#createAccessKeys",  async (done) => {
    await run();
    expect(mockCreateAccessKey).toHaveBeenCalled;
    done();
});
