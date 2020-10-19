const mockListAttacheRolePolicies = jest.fn();
jest.mock(
  "@aws-sdk/client-iam/commands/ListAttachedRolePoliciesCommand",
  () => ({
    IAM: function IAM() {
      this.ListAttachedRolePoliciesCommand = mockListAttacheRolePolicies;
    },
  })
);
const { params, run } = require("../../iam/src/iam_detachrolepolicy.js");

//test function
test("has to mock iam#detachrolepolicy", async (done) => {
  await run();
  expect(mockListAttacheRolePolicies).toHaveBeenCalled;
  done();
});
