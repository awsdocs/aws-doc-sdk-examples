import { describe, it, expect } from "vitest";
import { listDatastores } from "../actions/list-datastores.js";

describe("ListDatastores", () => {
  it("should return a datastores property that is an array", async () => {
    const response = await listDatastores();
    expect(response).toBeInstanceOf(Array);
  });
});
