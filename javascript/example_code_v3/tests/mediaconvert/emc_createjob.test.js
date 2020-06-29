process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
process.argv.push('--arg1', 'JOB_QUEUE_ARN');
process.argv.push('--arg1', 'IAM_ROLE_ARN');
process.argv.push('--arg1', 'OUTPUT_BUCKET_NAME');
process.argv.push('--arg1', 'INPUT_BUCKET_AND_FILENAME');
const mockCreateJob = jest.fn();
jest.mock('@aws-sdk/client-mediaconvert/commands/CreateJobCommand', () => ({
    MediaConvert: function MediaConvert() {
        this.CreateJobCommand = mockCreateJob
    }
}));
const {params, run} = require("../../mediaconvert/emc_createjob");

//test function
test("has to mock mediaconvert#createjob",  async (done) => {
    await run();
    expect(mockCreateJob).toHaveBeenCalled;
    done();
});
