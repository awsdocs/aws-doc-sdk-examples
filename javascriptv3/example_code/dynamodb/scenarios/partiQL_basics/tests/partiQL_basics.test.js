import {DeleteTableCommand} from "@aws-sdk/client-dynamodb";
import {describe, it, afterAll} from "vitest";
import {ddbDocClient} from "../libs/ddbDocClient.js";
import {main} from "../src/partiQL_basics.js";

describe("partiQL_basics#run", () => {
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
            "myMovieName",
            2022,
            "This Is the End",
            2013,
            "Amazon Studios",
            "../../../../../../resources/sample_files/movies.json"
        );
    })
});
