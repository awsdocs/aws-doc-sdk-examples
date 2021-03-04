const mockModifyCluster = jest.fn();
jest.mock("@aws-sdk/client-redshift-node", () => ({
    Redshift: function Redshift() {
        this.ModifyClusterCommand = mockModifyCluster;
    },
}));
const { run } = require("../../redshift/src/redshift-create-cluster");

test("has to mock RedShift#modifyClusters", async (done) => {
    await run();
    expect(mockModifyCluster).toHaveBeenCalled;
    done();
});
