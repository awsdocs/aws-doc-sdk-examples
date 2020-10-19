const mockDeleteReceiptRuleSet = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/DeleteReceiptRuleSetCommand", () => ({
  SES: function SES() {
    this.DeleteReceiptRuleSetCommand = mockDeleteReceiptRuleSet;
  },
}));
const { run } = require("../../ses/src/ses_deletetemplate.js");

//test function
test("has to mock SES#deletetemplate", async (done) => {
  await run();
  expect(mockDeleteReceiptRuleSet).toHaveBeenCalled;
  done();
});
