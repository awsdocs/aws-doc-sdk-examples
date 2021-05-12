const mockConfirmSubscription = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/ConfirmSubscriptionCommand", () => ({
  SNS: function SNS() {
    this.ConfirmSubscriptionCommand = mockConfirmSubscription;
  },
}));
const { run } = require("../../sns/src/sns_confirmsubscription");

test("has to mock SNS#confirmsubscription", async (done) => {
  await run();
  expect(mockConfirmSubscription).toHaveBeenCalled;
  done();
});
