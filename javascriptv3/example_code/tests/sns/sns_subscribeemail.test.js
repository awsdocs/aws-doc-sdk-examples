const mockSubscribe = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/SubscribeCommand", () => ({
  SNS: function SNS() {
    this.SubscribeCommand = mockSubscribe;
  },
}));
const { run } = require("../../sns/src/sns_subscribeemail.js");

test("has to mock SNS#subscribeemail", async (done) => {
  await run();
  expect(mockSubscribe).toHaveBeenCalled;
  done();
});
