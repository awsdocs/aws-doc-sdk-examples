process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
process.argv.push('--arg1', 'QUEUE_ARN');

const mockListJobs = jest.fn();
jest.mock('@aws-sdk/client-mediaconvert/commands/ListJobsCommand', () => ({
    MediaConvert: function MediaConvert() {
        this.ListJobsCommand = mockListJobs
    }
}));
const {params, run} = require("../../mediaconvert/emc_listjobs");

//test function
test("has to mock mediaconvert#canceljob",  async (done) => {
    await run();
    expect(mockListJobs).toHaveBeenCalled;
    done();
});
