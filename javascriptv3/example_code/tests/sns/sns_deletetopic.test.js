const mockDeleteTopic = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/DeleteTopicCommand", () => ({
  SNS: function SNS() {
    this.DeleteTopicCommand = mockDeleteTopic;
  },
}));
const { run } = require("../../sns/sns_deletetopic.js");

//test function
test("has to mock SNS#deletetopic", async (done) => {
  await run();
  expect(mockDeleteTopic).toHaveBeenCalled;
  done();
});
