const mockPublishCommand = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/PublishCommand", () => ({
  SNS: function SNS() {
    this.PublishCommand = mockPublishCommand;
  },
}));
const { run } = require("../../sns/sns_publishsms.js");

//test function
test("has to mock SNS#publishsms", async (done) => {
  await run();
  expect(mockPublishCommand).toHaveBeenCalled;
  done();
});
