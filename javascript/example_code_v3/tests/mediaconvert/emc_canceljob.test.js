process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
const mockCancelJob = jest.fn();
jest.mock('@aws-sdk/client-mediaconvert/commands/CancelJobCommand', () => ({
    MediaConvert: function MediaConvert() {
        this.CancelJobCommand = mockCancelJob
    }
}));
const {params, run} = require("../../mediaconvert/emc_canceljob");

//test function
test("has to mock mediaconvert#canceljob",  async (done) => {
    await run();
    expect(mockCancelJob).toHaveBeenCalled;
    done();
});
