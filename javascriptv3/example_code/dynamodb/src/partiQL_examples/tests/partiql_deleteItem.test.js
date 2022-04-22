import "regenerator-runtime/runtime";
import { run } from "../src/partiql_deleteItem";

const tableName = "Movies";
const movieYear1 = "2006";
const movieTitle1 = "The Departed";

const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName, movieYear1, movieTitle1);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
