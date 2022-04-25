import "regenerator-runtime/runtime";
import { run } from "../src/partiql_createTable.js";

function getRandomInt(max) {
  return Math.floor(Math.random() * max);
}
const value = getRandomInt(1000);
export const tableName = "Movies_" + value;
const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(tableName);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
