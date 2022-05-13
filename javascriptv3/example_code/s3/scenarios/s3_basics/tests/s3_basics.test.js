function getRandomInt(max) {
  return Math.floor(Math.random() * max);
}
const value = getRandomInt(1000);
const bucket_name = "bucket-" + value;
process.argv.push("node", "s3_basics.js", bucket_name, "test.txt", "Test Content");
console.log('argv', process.argv[4], process.argv[5], process.argv[6])
const expected = "Run successfully";

import "regenerator-runtime/runtime";
import { run } from "../src/s3_basics.js";

describe("Test function runs", () => {
  it("should successfully run",  async() => {
    /*    console.log(value);*/
    const response = await run(process.argv[4], process.argv[5], process.argv[6]);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual((expected));
  });
});
