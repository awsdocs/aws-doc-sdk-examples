






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
