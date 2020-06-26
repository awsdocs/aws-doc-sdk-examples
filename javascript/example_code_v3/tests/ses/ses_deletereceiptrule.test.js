process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'RULE_NAME');
process.argv.push('--arg3', 'RULE_SET_NAME');

const mockDeleteReceiptFilterRule = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/DeleteReceiptRuleCommand', () => ({
    SES: function SES() {
        this.DeleteReceiptRuleCommand = mockDeleteReceiptFilterRule
    }
}));
const {run} = require("../../ses/ses_deletereceiptrule.js");

//test function
test("has to mock SES#deletereceiptrule",  async (done) => {
    await run();
    expect(mockDeleteReceiptFilterRule).toHaveBeenCalled;
    done();
});
