


const mockUnscribe = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/UnsubscribeCommand', () => ({
    SNS: function SNS() {
        this.UnsubscribeCommand = mockUnscribe
    }
}));
const {run} = require("../../sns/sns_unsubscribe.js");

//test function
test("has to mock SNS#unsubscribe",  async (done) => {
    await run();
    expect(mockUnscribe).toHaveBeenCalled;
    done();
});
