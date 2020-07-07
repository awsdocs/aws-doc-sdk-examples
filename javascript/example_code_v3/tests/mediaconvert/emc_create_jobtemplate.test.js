


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
