process.argv.push('--arg1', 'ACCOUNT_ENDPOINT');
process.argv.push('--arg2', 'JOB_QUEUE_ARN');
process.argv.push('--arg3', 'BUCKET_NAME');
const mockCreateJobTemp = jest.fn();
jest.mock('@aws-sdk/client-mediaconvert/commands/CreateJobTemplateCommand', () => ({
    MediaConvert: function MediaConvert() {
        this.CreateJobTemplateCommand = mockCreateJobTemp
    }
}));
const {params, run} = require("../../mediaconvert/emc_create_jobtemplate");

//test function
test("has to mock mediaconvert#createjobtemplate",  async (done) => {
    await run();
    expect(mockCreateJobTemp).toHaveBeenCalled;
    done();
});
