process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'ORIGINGAL_USER_NAME');
process.argv.push('--arg3', 'NEW_USER_NAME');
const mockUpdateUser = jest.fn();
jest.mock('@aws-sdk/client-iam/commands/UpdateUserCommand', () => ({
    IAM: function IAM() {
        this.UpdateUserCommand = mockUpdateUser
    }
}));
const {params, run} = require("../../iam/iam_updateuser.js");

//test function
test("has to mock iam#updateuser",  async (done) => {
    await run();
    expect(mockUpdateUser).toHaveBeenCalled;
    done();
});
