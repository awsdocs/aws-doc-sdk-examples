const mockPutMetricData = jest.fn();
jest.mock("@aws-sdk/client-cloudwatch/commands/PutMetricDataCommand", () => ({
  CloudWatch: function CloudWatch() {
    this.PutMetricDataCommand = mockPutMetricData;
  },
}));
const { params, run } = require("../../cloudwatch/cw_putmetricalarm");

//test function
test("has to mock cloudwatch#putmetricalarm", async (done) => {
  await run();
  expect(mockPutMetricData).toHaveBeenCalled;
  done();
});
