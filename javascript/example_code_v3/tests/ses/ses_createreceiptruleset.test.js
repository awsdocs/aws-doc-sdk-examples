process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'RULE_SET_NAME');

const mockCreateReceiptFilter = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/CreateReceiptRuleSetCommand', () => ({
    SES: function SES() {
        this.CreateReceiptRuleSetCommand = mockCreateReceiptFilter
    }
}));
const {run} = require("../../ses/ses_createreceiptruleset.js");

//test function
test("has to mock SES#ses_createreceiptruleset",  async (done) => {
    await run();
    expect(mockCreateReceiptFilter).toHaveBeenCalled;
    done();
});
