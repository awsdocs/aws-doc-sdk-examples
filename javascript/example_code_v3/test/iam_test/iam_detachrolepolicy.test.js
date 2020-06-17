process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'ROLE_NAME');
const mockListAttacheRolePolicies = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/ListAttachedRolePoliciesCommand', () => ({
    IAM: function IAM() {
        this.ListAttachedRolePoliciesCommand = mockListAttacheRolePolicies
    }
}));
const {params, run} = require("../../iam/iam_detachrolepolicy.js");

//test function
test("has to mock iam#detachrolepolicy",  async (done) => {
    await run();
    expect(mockListAttacheRolePolicies).toHaveBeenCalled;
    done();
});
