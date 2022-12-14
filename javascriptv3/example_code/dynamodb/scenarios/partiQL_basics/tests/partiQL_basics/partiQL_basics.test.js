import {DeleteTableCommand} from "@aws-sdk/client-dynamodb";
import {describe, it, afterAll} from "vitest";
import {ddbDocClient} from "../../libs/ddbDocClient.js";
import {runScenario} from "../../src/partiQL_basics.js";

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
        await runScenario(
            {
                tableName: "myNewTable",
                newMovieName: "myMovieName",
                newMovieYear: 2022,
                existingMovieName: "This Is the End",
                existingMovieYear: 2013,
                newProducer: "Amazon Movies",
                moviesPath: "../../../../../../../resources/sample_files/movies.json",
            }
        );
    })
});
