
const mockListAccountAliases = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/ListAccountAliasesCommand', () => ({
    IAM: function IAM() {
        this.ListAccountAliasesCommand = mockListAccountAliases
    }
}));
const {params, run} = require("../../iam/iam_listusers.js");

//test function
test("has to mock iam#listusers",  async (done) => {
    await run();
    expect(mockListAccountAliases).toHaveBeenCalled;
    done();
});
