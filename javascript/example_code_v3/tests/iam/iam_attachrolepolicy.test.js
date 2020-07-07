

const mockListAttRolePolicies = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/ListAttachedRolePoliciesCommand', () => ({
    IAM: function IAM() {
        this.ListAttachedRolePoliciesCommand = mockListAttRolePolicies
    }
}));
const {params, run} = require("../../iam/iam_attachrolepolicy.js");

//test function
test("has to mock iam#listAttachedPolicies",  async (done) => {
    await run();
    expect(mockListAttRolePolicies).toHaveBeenCalled;
    done();
});
