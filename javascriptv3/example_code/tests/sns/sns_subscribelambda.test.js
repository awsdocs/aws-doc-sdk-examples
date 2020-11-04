const mockSubscribeLambda = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/SubscribeCommand", () => ({
  SNS: function SNS() {
    this.SubscribeCommand = mockSubscribeLambda;
  },
}));
const { run } = require("../../sns/src/sns_subscribelambda.js");

test("has to mock SNS#subscribelambda", async (done) => {
  await run();
  expect(mockSubscribeLambda).toHaveBeenCalled;
  done();
});
