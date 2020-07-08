const mockDeleteReceiptFilterRule = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/DeleteReceiptRuleCommand", () => ({
  SES: function SES() {
    this.DeleteReceiptRuleCommand = mockDeleteReceiptFilterRule;
  },
}));
const { run } = require("../../ses/ses_deletereceiptrule.js");

//test function
test("has to mock SES#deletereceiptrule", async (done) => {
  await run();
  expect(mockDeleteReceiptFilterRule).toHaveBeenCalled;
  done();
});
