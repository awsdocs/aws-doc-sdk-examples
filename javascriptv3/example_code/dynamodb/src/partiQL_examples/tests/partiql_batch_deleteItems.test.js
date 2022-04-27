import "regenerator-runtime/runtime";
import { run } from "../src/partiql_batch_deleteItems";

const tableName = "Movies_batch";
const movieYear1 = "2006";
const movieTitle1 = "The Departed";
const movieYear2 = "2013";
const movieTitle2 = "2 Guns";

const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(
      tableName,
      movieYear1,
      movieTitle1,
      movieYear2,
      movieTitle2
    );
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
