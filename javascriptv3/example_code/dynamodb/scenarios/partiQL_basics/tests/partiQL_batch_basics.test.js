import {DeleteTableCommand} from "@aws-sdk/client-dynamodb";
import {describe, it, afterAll} from "vitest";
import {ddbDocClient} from "../libs/ddbDocClient.js";
import {main} from "../src/partiQL_batch_basics.js";

describe("partiQL_batch_basics#run", () => {
    afterAll(async () => {
        const command = new DeleteTableCommand({TableName: "myNewTable"});
        try {
            await ddbDocClient.send(command);
        } catch (err) {
            console.error(err);
        }
    });

    it("should successfully run", async () => {
        await main(
           "myNewTable",
           "myMovieName1",
           2022,
           "myMovieName2",
            2023,
            "This Is the End",
            2013,
           "Deep Impact",
            1998,
            "Amazon Movies",
            "Amazon Movies2",
            "../../../../../../resources/sample_files/movies.json",
        );
    })
});
