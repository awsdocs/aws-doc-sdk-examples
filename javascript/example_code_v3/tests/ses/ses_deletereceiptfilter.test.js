


const mockDeleteReceiptFilter = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/DeleteReceiptFilterCommand', () => ({
    SES: function SES() {
        this.DeleteReceiptFilterCommand = mockDeleteReceiptFilter
    }
}));
const {run} = require("../../ses/ses_deletereceiptfilter.js");

//test function
test("has to mock SES#deleteRecepiptFilter",  async (done) => {
    await run();
    expect(mockDeleteReceiptFilter).toHaveBeenCalled;
    done();
});
