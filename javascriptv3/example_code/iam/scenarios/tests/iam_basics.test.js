function getRandomInt(max) {
  return Math.floor(Math.random() * max);
}
const value = getRandomInt(1000);
var userName = "test-user-" + value;
var s3_policy_name = "s3-policy-" + value;
var role_name = "iam-role-" + value;
var assume_policy_name = "assume-role-" + value;
process.argv.push("node", "iam_basics.js", userName, s3_policy_name, role_name, assume_policy_name);
console.log('argv', process.argv[4], process.argv[5], process.argv[6], process.argv[7])
const expected = "Run successfully";

import "regenerator-runtime/runtime";
import { run } from "../src/iam_basics.js";
jest.setTimeout(50000);
describe("Test function runs", () => {
  it("should successfully run",  async() => {
     const response = await run(process.argv[4], process.argv[5], process.argv[6], process.argv[7]);
    console.log("Response ", Promise.resolve(response));
    expect(response).toEqual((expected));
  });
});



