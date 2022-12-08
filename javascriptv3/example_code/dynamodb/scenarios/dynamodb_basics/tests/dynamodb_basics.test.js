import { DeleteCommand } from "@aws-sdk/lib-dynamodb";
import { describe, it, afterAll } from "vitest";
import { ddbDocClient } from "../libs/ddbDocClient.js";
import { runScenario } from "../src/dynamodb_basics.js";

describe("dynamodb_basics#run", () => {
  afterAll(async () => {
    const command = new DeleteCommand({ TableName: "myNewTable" });
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
          newMovieRank: 200,
          newMoviePlot: "A coder cracks code...",
          moviesPath: "../../../../../../resources/sample_files/movies.json",
        }
    );
  });
});
