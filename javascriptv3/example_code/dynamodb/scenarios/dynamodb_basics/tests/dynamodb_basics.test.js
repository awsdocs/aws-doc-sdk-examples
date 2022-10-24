process.argv.push("node", "dynamodb_basics.js", "myNewTable", "myMovieName", 2022, "This Is the End", 2013, 200, "A coder cracks code...");
console.log('argv', process.argv[4], process.argv[5], process.argv[6], process.argv[7], process.argv[8], process.argv[9], process.argv[10]);

import "regenerator-runtime/runtime";
import { run } from "../src/dynamodb_basics.js";
jest.setTimeout(50000);

const expected = "Run successfully";

describe("Test function runs", () => {
  it("should successfully run", async () => {
    const response = await run(
        process.argv[4],
        process.argv[5],
        process.argv[6],
        process.argv[7],
        process.argv[8],
        process.argv[9],
        process.argv[10]
    );
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual(expected);
  });
});
