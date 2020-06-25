process.argv.push('--arg1', 'us-west-2');
const mockListAccountAliases = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/ListAccountAliasesCommand', () => ({
    IAM: function IAM() {
        this.ListAccountAliasesCommand = mockListAccountAliases
    }
}));
const {params, run} = require("../../iam/iam_listaccountaliases.js");

//test function
test("has to mock iam#listaccountaliases",  async (done) => {
    await run();
    expect(mockListAccountAliases).toHaveBeenCalled;
    done();
});
