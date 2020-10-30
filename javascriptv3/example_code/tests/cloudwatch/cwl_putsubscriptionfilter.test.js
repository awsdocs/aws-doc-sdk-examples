const mockPutSubFilter = jest.fn();
jest.mock(
  "@aws-sdk/client-cloudwatch-logs/commands/PutSubscriptionFilterCommand",
  () => ({
    CloudWatch: function CloudWatch() {
      this.PutSubscriptionFilterCommand = mockPutSubFilter;
    },
  })
);
const { params, run } = require("../../cloudwatch/src/cwl_putsubscriptionfilter");

//test function
test("has to mock cloudwatch-logs#putsubscriptionfilter", async (done) => {
  await run();
  expect(mockPutSubFilter).toHaveBeenCalled;
  done();
});
