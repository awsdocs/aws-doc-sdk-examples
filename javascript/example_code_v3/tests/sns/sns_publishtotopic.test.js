


const mockPublish = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/PublishCommand', () => ({
    SNS: function SNS() {
        this.PublishCommand = mockPublish
    }
}));
const {run} = require("../../sns/sns_publishtotopic.js");

//test function
test("has to mock SNS#publishtotopic",  async (done) => {
    await run();
    expect(mockPublish).toHaveBeenCalled;
    done();
});
