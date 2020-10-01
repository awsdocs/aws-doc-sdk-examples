const mockDeleteCluster = jest.fn();
jest.mock("@aws-sdk/client-redshift-node", () => ({
    Redshift: function Redshift() {
        this.DeleteClusterCommand = mockDeleteCluster;
    },
}));
const { run } = require("../../redshift/redshift-create-cluster.ts");

//test function
test("has to mock RedShift#deleteCluster", async (done) => {
    await run();
    expect(mockDeleteCluster).toHaveBeenCalled;
    done();
});
