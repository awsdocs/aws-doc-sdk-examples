const mockDeleteTopic = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/DeleteTopicCommand", () => ({
  SNS: function SNS() {
    this.DeleteTopicCommand = mockDeleteTopic;
  },
}));
const { run } = require("../../sns/src/sns_deletetopic.js");

test("has to mock SNS#deletetopic", async (done) => {
  await run();
  expect(mockDeleteTopic).toHaveBeenCalled;
  done();
});
