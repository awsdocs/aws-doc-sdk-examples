const mockListAccessKeys = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/ListAccessKeysCommand", () => ({
  IAM: function IAM() {
    this.ListAccessKeysCommand = mockListAccessKeys;
  },
}));
const { params, run } = require("../../iam/src/iam_listaccesskeys.js");

test("has to mock iam#listAccessKeys", async (done) => {
  await run();
  expect(mockListAccessKeys).toHaveBeenCalled;
  done();
});
