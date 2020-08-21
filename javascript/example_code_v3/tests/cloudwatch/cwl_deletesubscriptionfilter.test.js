const mockDeletSubFilter = jest.fn();
jest.mock(
  "@aws-sdk/client-cloudwatch-logs/commands/DeleteSubscriptionFilterCommand",
  () => ({
    CloudWatch: function CloudWatch() {
      this.DeleteSubscriptionFilterCommand = mockDeletSubFilter;
    },
  })
);
const {
  params,
  run,
} = require("../../cloudwatch/cwl_deletesubscriptionfilter");

//test function
test("has to mock cloudwatch-logs#deletesubscriptionfilter", async (done) => {
  await run();
  expect(mockDeletSubFilter).toHaveBeenCalled;
  done();
});
