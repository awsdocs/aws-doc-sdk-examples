const mockDescribeClusters = jest.fn();
jest.mock("@aws-sdk/client-redshift-node", () => ({
    Redshift: function Redshift() {
        this.DescribeClustersCommand = mockDescribeClusters;
    },
}));
const { run } = require("../../redshift/src/redshift-create-cluster");

test("has to mock RedShift#describeClusters", async (done) => {
    await run();
    expect(mockDescribeClusters).toHaveBeenCalled;
    done();
});
