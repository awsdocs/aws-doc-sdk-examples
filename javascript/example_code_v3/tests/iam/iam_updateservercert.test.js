const mockUpdateServerCert = jest.fn();
jest.mock(
  "@aws-sdk/client-iam/commands/UpdateServerCertificateCommand",
  () => ({
    IAM: function IAM() {
      this.UpdateServerCertificateCommand = mockUpdateServerCert;
    },
  })
);
const { params, run } = require("../../iam/iam_updateservercert.js");

//test function
test("has to mock iam#updateservercert", async (done) => {
  await run();
  expect(mockUpdateServerCert).toHaveBeenCalled;
  done();
});
