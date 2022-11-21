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
      "myNewTable",
      "myMovieName",
      2022,
      "This Is the End",
      2013,
      200,
      "A coder cracks code..."
    );
  });
});
