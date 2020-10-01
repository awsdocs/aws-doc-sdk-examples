const mockDeleteAccountAlias = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/DeleteAccountAliasCommand", () => ({
  IAM: function IAM() {
    this.DeleteAccountAliasCommand = mockDeleteAccountAlias;
  },
}));
const { params, run } = require("../../iam/iam_deleteaccountalias.js");

//test function
test("has to mock iam#deleteaccontalias", async (done) => {
  await run();
  expect(mockDeleteAccountAlias).toHaveBeenCalled;
  done();
});
