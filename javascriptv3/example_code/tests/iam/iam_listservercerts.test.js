const mockListServerCertificates = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/ListServerCertificatesCommand", () => ({
  IAM: function IAM() {
    this.ListServerCertificatesCommand = mockListServerCertificates;
  },
}));
const { params, run } = require("../../iam/iam_listservercerts.js");

//test function
test("has to mock iam#listservercerts", async (done) => {
  await run();
  expect(mockListServerCertificates).toHaveBeenCalled;
  done();
});
