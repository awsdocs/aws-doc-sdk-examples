import "regenerator-runtime/runtime";
import { run } from "../src/partiql_getItem.js";

const tableName = "Movies";
const movieYear1 = "2006";
const movieTitle1 = "The Departed";
const producer1 = "New View Films";

const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName, movieYear1, movieTitle1, producer1);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
