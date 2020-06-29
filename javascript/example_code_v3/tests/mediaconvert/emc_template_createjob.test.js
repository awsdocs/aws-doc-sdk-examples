process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
process.argv.push('--arg1', 'QUEUE_ARN');
process.argv.push('--arg1', 'TEMPLATE_NAME');
process.argv.push('--arg1', 'ROLE_ARN');
process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
process.argv.push('--arg1', 'INPUT_BUCKET_AND_FILENAME');

const mockCreateTempJob = jest.fn();
jest.mock('@aws-sdk/client-mediaconvert/commands/CreateJobCommand', () => ({
    MediaConvert: function MediaConvert() {
        this.CreateJobCommand = mockCreateTempJob
    }
}));
const {params, run} = require("../../mediaconvert/emc_template_createjob");

//test function
test("has to mock mediaconvert#templatecreatejob",  async (done) => {
    await run();
    expect(mockCreateTempJob).toHaveBeenCalled;
    done();
});
