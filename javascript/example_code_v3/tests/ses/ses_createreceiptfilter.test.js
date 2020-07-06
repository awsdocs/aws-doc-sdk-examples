process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', '10.0.0.1');
process.argv.push('--arg3', 'ALLOW');
process.argv.push('--arg4', 'NAME');

const mockCreateReceiptFilter = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/CreateReceiptFilterCommand', () => ({
    SES: function SES() {
        this.CreateReceiptFilterCommand = mockCreateReceiptFilter
    }
}));
const {run} = require("../../ses/ses_createreceiptfilter");

//test function
test("has to mock SES#createRecepiptFilter",  async (done) => {
    await run();
    expect(mockCreateReceiptFilter).toHaveBeenCalled;
    done();
});
