const mockUnscribe = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/UnsubscribeCommand", () => ({
  SNS: function SNS() {
    this.UnsubscribeCommand = mockUnscribe;
  },
}));
const { run } = require("../../sns/src/sns_unsubscribe.js");

test("has to mock SNS#unsubscribe", async (done) => {
  await run();
  expect(mockUnscribe).toHaveBeenCalled;
  done();
});
