const mockListTopics = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/ListTopicsCommand", () => ({
  SNS: function SNS() {
    this.ListTopicsCommand = mockListTopics;
  },
}));
const { run } = require("../../sns/src/sns_listtopics.js");

//test function
test("has to mock SNS#listtopics", async (done) => {
  await run();
  expect(mockListTopics).toHaveBeenCalled;
  done();
});
