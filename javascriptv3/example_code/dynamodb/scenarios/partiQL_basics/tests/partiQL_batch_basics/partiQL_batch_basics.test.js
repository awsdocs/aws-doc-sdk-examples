import {DeleteTableCommand} from "@aws-sdk/client-dynamodb";
import {describe, it, afterAll} from "vitest";
import {ddbDocClient} from "../../libs/ddbDocClient.js";
import {runScenario} from "../../src/partiQL_batch_basics.js";

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
        await runScenario({
                tableName: "myNewTable",
                newMovieName1: "myMovieName1",
                newMovieYear1: 2022,
                newMovieName2: "myMovieName2",
                newMovieYear2: 2023,
                existingMovieName1: "This Is the End",
                existingMovieYear1: 2013,
                existingMovieName2: "Deep Impact",
                existingMovieYear2: 1998,
                newProducer1: "Amazon Movies",
                newProducer2: "Amazon Movies2",
                moviesPath: "../../../../../../../resources/sample_files/movies.json",
            }
        );
    })
});
