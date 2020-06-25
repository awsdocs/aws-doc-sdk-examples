process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'CERTIFICATE_NAME');
const mockGetServerCert = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/GetServerCertificateCommand', () => ({
    IAM: function IAM() {
        this.GetServerCertificateCommand = mockGetServerCert
    }
}));
const {params, run} = require("../../iam/iam_getservercert.js");

//test function
test("has to mock iam#getservercert",  async (done) => {
    await run();
    expect(mockGetServerCert).toHaveBeenCalled;
    done();
});
