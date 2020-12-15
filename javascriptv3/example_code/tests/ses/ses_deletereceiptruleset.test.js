const mockDeleteReceiptRuleSet = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/DeleteReceiptRuleSetCommand", () => ({
  SES: function SES() {
    this.DeleteReceiptRuleSetCommand = mockDeleteReceiptRuleSet;
  },
}));
const { run } = require("../../ses/src/ses_deletereceiptruleset.js");

test("has to mock SES#deletereceiptruleset", async (done) => {
  await run();
  expect(mockDeleteReceiptRuleSet).toHaveBeenCalled;
  done();
});
