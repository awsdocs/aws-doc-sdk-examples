import { describe, it, expect } from "@jest/globals";
import { Item } from "src/types/item.js";
import { parseItem } from "../src/handlers/parse-item.js";

describe("parseItem", () => {
  it("should convert a DBRecord into an Item", () => {
    const record: DBRecord = [
      { stringValue: "abcd-1234-asfd-sdff" },
      { stringValue: "Test csv maker." },
      { stringValue: "js" },
      { stringValue: "in-progress" },
      { stringValue: "corepyle" },
      { longValue: 0 },
    ];

    const item: Item = {
      archived: false,
      description: "Test csv maker.",
      guide: "js",
      id: "abcd-1234-asfd-sdff",
      name: "corepyle",
      status: "in-progress",
    };

    const actual = parseItem(record);

    expect(actual).toEqual(item);
  });
});
