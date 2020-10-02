const mockListPhoneNumberOptedOut = jest.fn();
jest.mock(
  "@aws-sdk/client-sns/commands/ListPhoneNumbersOptedOutCommand",
  () => ({
    SNS: function SNS() {
      this.ListPhoneNumbersOptedOutCommand = mockListPhoneNumberOptedOut;
    },
  })
);
const { run } = require("../../sns/sns_listnumbersoptedout.js");

//test function
test("has to mock SNS#listnumbersoptedout", async (done) => {
  await run();
  expect(mockListPhoneNumberOptedOut).toHaveBeenCalled;
  done();
});
