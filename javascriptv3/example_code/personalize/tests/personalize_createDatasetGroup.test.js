import 'dotenv/config'
function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}
const value = getRandomInt(1000);

// Set the schema parameters.
const createDatasetGroupParam = {
    name: process.env.DATASET_GROUP_NAME  /* required */
}
const expected = "Run successfully";

import "regenerator-runtime/runtime";
import { run } from "../src/personalize_createDatasetGroup.js";

describe("Test function runs", () => {
    it("should successfully run",  async() => {
        /*    console.log(value);*/
        const response = await run(createDatasetGroupParam);
        console.log("Response ", Promise.resolve(response));
        expect(response).toEqual((expected));
    });
});
