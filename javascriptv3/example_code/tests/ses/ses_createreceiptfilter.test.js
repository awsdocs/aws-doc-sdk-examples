const mockCreateReceiptFilter = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/CreateReceiptFilterCommand", () => ({
  SES: function SES() {
    this.CreateReceiptFilterCommand = mockCreateReceiptFilter;
  },
}));
const { run } = require("../../ses/src/ses_createreceiptfilter");

//test function
test("has to mock SES#createRecepiptFilter", async (done) => {
  await run();
  expect(mockCreateReceiptFilter).toHaveBeenCalled;
  done();
});
