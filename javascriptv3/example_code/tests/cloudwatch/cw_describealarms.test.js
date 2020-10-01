const mockDescribeAlarms = jest.fn();
jest.mock("@aws-sdk/client-cloudwatch/commands/DescribeAlarmsCommand", () => ({
  CloudWatch: function CloudWatch() {
    this.DescribeAlarmsCommand = mockDescribeAlarms;
  },
}));
const { params, run } = require("../../cloudwatch/cw_describealarms");

//test function
test("has to mock cloudwatch#describealarms", async (done) => {
  await run();
  expect(mockDescribeAlarms).toHaveBeenCalled;
  done();
});
