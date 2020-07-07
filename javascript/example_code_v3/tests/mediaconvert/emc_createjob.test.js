




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
