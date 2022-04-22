const tableName = "Movies_batch";
const movieYear1 = "2006";
const movieTitle1 = "The Departed";
const movieYear2 = "2013";
const movieTitle2 = "2 Guns";
const producer1 = "Old Thyme Films";
const producer2 = "New View Films";
const expected = "Run successfully";
import "regenerator-runtime/runtime";
import { run } from "../src/partiQL_batch_basics.js";
jest.setTimeout(50000);
describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(
      tableName,
      movieYear1,
      movieTitle1,
      movieYear2,
      movieTitle2,
      producer1,
      producer2
    );
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
