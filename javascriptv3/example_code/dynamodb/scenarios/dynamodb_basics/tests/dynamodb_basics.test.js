var tableName = "dynamodb-scenario-table-name";
var newMovieName = "new-movie-name";
var newMovieYear = 2022;
var existingMovieName = "Rush";
var existingMovieYear = 2013;
var newMovieRank = 111;
var newMoviePlot = "movie-plot";

const expected = "Run successfully";

import "regenerator-runtime/runtime";
import { run } from "../src/dynamodb_basics.js";
jest.setTimeout(50000);
describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(
      tableName,
      newMovieName,
      newMovieYear,
      existingMovieName,
      existingMovieYear,
      newMovieRank,
      newMoviePlot
    );
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
