

const mockPutEvents = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-events/commands/PutEventsCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutEventsCommand = mockPutEvents
    }
}));
const {params, run} = require("../../cloudwatch/cwe_putevents");

//test function
test("has to mock cloudwatch-events#putevents",  async (done) => {
    await run();
    expect(mockPutEvents).toHaveBeenCalled;
    done();
});
