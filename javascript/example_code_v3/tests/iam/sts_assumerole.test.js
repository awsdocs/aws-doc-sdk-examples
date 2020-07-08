const mockAssumeRole = jest.fn();
jest.mock("@aws-sdk/client-sts/commands/AssumeRoleCommand", () => ({
  IAM: function IAM() {
    this.AssumeRoleCommand = mockAssumeRole;
  },
}));
const { params, run } = require("../../iam/sts_assumerole.js");

//test function
test("has to mock iam#assumerole", async (done) => {
  await run();
  expect(mockAssumeRole).toHaveBeenCalled;
  done();
});
