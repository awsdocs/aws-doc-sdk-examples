process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'RULE_SET_NAME');

const mockDeleteReceiptRuleSet = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/DeleteReceiptRuleSetCommand', () => ({
    SES: function SES() {
        this.DeleteReceiptRuleSetCommand = mockDeleteReceiptRuleSet
    }
}));
const {run} = require("../../ses/ses_deletereceiptruleset.js");

//test function
test("has to mock SES#deletereceiptruleset",  async (done) => {
    await run();
    expect(mockDeleteReceiptRuleSet).toHaveBeenCalled;
    done();
});
