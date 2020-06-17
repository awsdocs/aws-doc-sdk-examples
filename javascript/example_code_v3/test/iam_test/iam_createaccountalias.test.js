process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'ACCOUNT_ALIAS');
const mockCreateAccountAlias = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/GetAccessKeyLastUsedCommand', () => ({
    IAM: function IAM() {
        this.CreateAccountAliasCommand = mockCreateAccountAlias
    }
}));
const {params, run} = require("../../iam/iam_createaccountalias.js");

//test function
test("has to mock iam#createaccountalias",  async (done) => {
    await run();
    expect(mockCreateAccountAlias).toHaveBeenCalled;
    done();
});
