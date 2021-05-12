const mockGetTopicAttributes = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/GetTopicAttributesCommand", () => ({
  SNS: function SNS() {
    this.GetTopicAttributesCommand = mockGetTopicAttributes;
  },
}));
const { run } = require("../../sns/src/sns_gettopicattributes.js");

test("has to mock SNS#gettopicattributes", async (done) => {
  await run();
  expect(mockGetTopicAttributes).toHaveBeenCalled;
  done();
});
