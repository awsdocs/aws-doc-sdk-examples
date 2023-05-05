import { describe, it } from "vitest";
import { client } from "../client.js";
import { ListUsersCommand } from "@aws-sdk/client-iam";

describe("client", () => {
  it("should successfully run a command", async () => {
    await client.send(new ListUsersCommand({}));
  });
});
