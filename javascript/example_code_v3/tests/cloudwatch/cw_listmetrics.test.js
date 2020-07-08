const mockListMetrics = jest.fn();
jest.mock("@aws-sdk/client-cloudwatch/commands/PutMetricAlarmCommand", () => ({
  CloudWatch: function CloudWatch() {
    this.PutMetricAlarmCommand = mockListMetrics;
  },
}));
const { params, run } = require("../../cloudwatch/cw_listmetrics");

//test function
test("has to mock cloudwatch#listmetrics", async (done) => {
  await run();
  expect(mockListMetrics).toHaveBeenCalled;
  done();
});
