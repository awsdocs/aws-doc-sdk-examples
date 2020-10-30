const mockCreateTopic = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/CreateTopicCommand", () => ({
  SNS: function SNS() {
    this.CreateTopicCommand = mockCreateTopic;
  },
}));
const { run } = require("../../sns/src/sns_createtopic.js");

//test function
test("has to mock SNS#createtopic", async (done) => {
  await run();
  expect(mockCreateTopic).toHaveBeenCalled;
  done();
});
