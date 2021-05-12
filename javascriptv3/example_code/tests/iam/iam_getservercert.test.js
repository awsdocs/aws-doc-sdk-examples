const mockGetServerCert = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/GetServerCertificateCommand", () => ({
  IAM: function IAM() {
    this.GetServerCertificateCommand = mockGetServerCert;
  },
}));
const { params, run } = require("../../iam/src/iam_getservercert.js");

test("has to mock iam#getservercert", async (done) => {
  await run();
  expect(mockGetServerCert).toHaveBeenCalled;
  done();
});
