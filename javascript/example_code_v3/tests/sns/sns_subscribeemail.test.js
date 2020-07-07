




const mockSubscribe = jest.fn();
jest.mock('@aws-sdk/client-sns/commands/SubscribeCommand', () => ({
    SNS: function SNS() {
        this.SubscribeCommand = mockSubscribe
    }
}));
const {run} = require("../../sns/sns_subscribeemail.js");

//test function
test("has to mock SNS#subscribeemail",  async (done) => {
    await run();
    expect(mockSubscribe).toHaveBeenCalled;
    done();
});
