const mockDeleteServerCert = jest.fn();
jest.mock(
  "@aws-sdk/client-iam/commands/DeleteServerCertificateCommand",
  () => ({
    IAM: function IAM() {
      this.DeleteServerCertificateCommand = mockDeleteServerCert;
    },
  })
);
const { params, run } = require("../../iam/iam_deleteservercert.js");

//test function
test("has to mock iam#deleteservercertificate", async (done) => {
  await run();
  expect(mockDeleteServerCert).toHaveBeenCalled;
  done();
});
