process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'S3_BUCKET_NAME');
process.argv.push('--arg3', 'EMAIL_ADDRESS');
process.argv.push('--arg4', 'RULE_NAME');
process.argv.push('--arg5', 'RULE_SET_NAME');

const mockCreateReceiptRule = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/CreateReceiptRuleCommand', () => ({
    SES: function SES() {
        this.CreateReceiptRuleCommand = mockCreateReceiptRule
    }
}));
const {run} = require("../../ses/ses_createreceiptrule.js");

//test function
test("has to mock SES#createreceiptrule",  async (done) => {
    await run();
    expect(mockCreateReceiptRule).toHaveBeenCalled;
    done();
});
