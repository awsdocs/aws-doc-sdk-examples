const mockSetTopicAttributes = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/SetTopicAttributesCommand", () => ({
  SNS: function SNS() {
    this.SetTopicAttributesCommand = mockSetTopicAttributes;
  },
}));
const { run } = require("../../sns/sns_settopicattributes.js");

//test function
test("has to mock SNS#settopicattributes", async (done) => {
  await run();
  expect(mockSetTopicAttributes).toHaveBeenCalled;
  done();
});
