import {
  CreateSecretCommand,
  DeleteSecretCommand,
  SecretsManagerClient,
} from "@aws-sdk/client-secrets-manager";
import { describe, it, expect, afterAll } from "vitest";
import { getSecretValue } from "../actions/get-secret-value.js";

describe("secrets manager examples", () => {
  const client = new SecretsManagerClient({});
  const secretNames = [];

  afterAll(async () => {
    for (const name of secretNames) {
      await client.send(new DeleteSecretCommand({ SecretId: name }));
    }
  });

  describe("getSecretValue", () => {
    it("should return a byte array if the value is binary data", async () => {
      // Upload binary secret
      const rand = Math.floor(Math.random() * 10000000);
      const name = `binary-secret-${rand}`;
      secretNames.push(name);
      const command = new CreateSecretCommand({
        Name: name,
        SecretBinary: Buffer.from("binary data"),
      });

      await client.send(command);
      const response = await getSecretValue(name);
      expect(response).toEqual(Uint8Array.from(Buffer.from("binary data")));
    });
  });
});
