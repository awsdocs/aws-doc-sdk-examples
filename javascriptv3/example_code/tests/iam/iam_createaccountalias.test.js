const mockCreateAccountAlias = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/GetAccessKeyLastUsedCommand", () => ({
  IAM: function IAM() {
    this.CreateAccountAliasCommand = mockCreateAccountAlias;
  },
}));
const { params, run } = require("../../iam/src/iam_createaccountalias.js");

//test function
test("has to mock iam#createaccountalias", async (done) => {
  await run();
  expect(mockCreateAccountAlias).toHaveBeenCalled;
  done();
});
