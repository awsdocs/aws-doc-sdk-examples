const mockListAccountAliases = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/ListAccountAliasesCommand", () => ({
  IAM: function IAM() {
    this.ListAccountAliasesCommand = mockListAccountAliases;
  },
}));
const { params, run } = require("../../iam/src/iam_listaccountaliases.js");

//test function
test("has to mock iam#listaccountaliases", async (done) => {
  await run();
  expect(mockListAccountAliases).toHaveBeenCalled;
  done();
});
