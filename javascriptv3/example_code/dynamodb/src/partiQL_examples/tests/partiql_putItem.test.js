import "regenerator-runtime/runtime";
import { run } from "../src/partiql_getItem.js";

const tableName = "Movies";
const movieTitle1 = "The Departed";

const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName, movieTitle1);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
