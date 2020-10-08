const mockCreateReceiptRule = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/CreateReceiptRuleCommand", () => ({
  SES: function SES() {
    this.CreateReceiptRuleCommand = mockCreateReceiptRule;
  },
}));
const { run } = require("../../ses/src/ses_createreceiptrule.js");

//test function
test("has to mock SES#createreceiptrule", async (done) => {
  await run();
  expect(mockCreateReceiptRule).toHaveBeenCalled;
  done();
});
