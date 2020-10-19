const mockSetSMSAttributes = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/SetSMSAttributesCommand", () => ({
  SNS: function SNS() {
    this.SetSMSAttributesCommand = mockSetSMSAttributes;
  },
}));
const { run } = require("../../sns/src/sns_setsmstype.js");

//test function
test("has to mock SNS#setsmstype", async (done) => {
  await run();
  expect(mockSetSMSAttributes).toHaveBeenCalled;
  done();
});
