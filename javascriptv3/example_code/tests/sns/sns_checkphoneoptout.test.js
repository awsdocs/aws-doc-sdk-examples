const mockCheckPhoneOptOut = jest.fn();
jest.mock(
  "@aws-sdk/client-sns/commands/CheckIfPhoneNumberIsOptedOutCommand",
  () => ({
    SNS: function SNS() {
      this.CheckIfPhoneNumberIsOptedOutCommand = mockCheckPhoneOptOut;
    },
  })
);
const { run } = require("../../sns/src/sns_checkphoneoptout.js");

//test function
test("has to mock SNS#checkphoneoptout", async (done) => {
  await run();
  expect(mockCheckPhoneOptOut).toHaveBeenCalled;
  done();
});
