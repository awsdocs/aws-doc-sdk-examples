const mockPutMetricAlarm = jest.fn();
jest.mock("@aws-sdk/client-cloudwatch/commands/PutMetricAlarmCommand", () => ({
  CloudWatch: function CloudWatch() {
    this.PutMetricAlarmCommand = mockPutMetricAlarm;
  },
}));
const { params, run } = require("../../cloudwatch/src/cw_putmetricalarm");

test("has to mock cloudwatch#putmetricalarm", async (done) => {
  await run();
  expect(mockPutMetricAlarm).toHaveBeenCalled;
  done();
});
