process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'ACCESS_KEY_ID');
process.argv.push('--arg3', 'USER_NAME');
const mockUpdateServerCert = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/UpdateServerCertificateCommand', () => ({
    IAM: function IAM() {
        this.UpdateServerCertificateCommand = mockUpdateServerCert
    }
}));
const {params, run} = require("../../iam/iam_updateservercert.js");

//test function
test("has to mock iam#updateservercert",  async (done) => {
    await run();
    expect(mockUpdateServerCert).toHaveBeenCalled;
    done();
});
