const mockCreateReceiptFilter = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/CreateReceiptRuleSetCommand", () => ({
  SES: function SES() {
    this.CreateReceiptRuleSetCommand = mockCreateReceiptFilter;
  },
}));
const { run } = require("../../ses/src/ses_createreceiptruleset.js");

test("has to mock SES#ses_createreceiptruleset", async (done) => {
  await run();
  expect(mockCreateReceiptFilter).toHaveBeenCalled;
  done();
});
