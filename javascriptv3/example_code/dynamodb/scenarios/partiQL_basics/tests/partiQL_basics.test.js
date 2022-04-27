var tableName = "Movies";
var movieYear1 = "2006";
var movieTitle1 = "The Departed";
var producer1 = "New View Films";

const expected = "Run successfully";

import "regenerator-runtime/runtime";
import { run } from "../src/partiQL_basics.js";
jest.setTimeout(50000);
describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName, movieYear1, movieTitle1, producer1);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
