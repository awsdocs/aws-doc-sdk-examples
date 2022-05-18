import "regenerator-runtime/runtime";
import { run } from "../src/partiql_batchwriteitems.js";

const tableName = "Movies_batch";
const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
