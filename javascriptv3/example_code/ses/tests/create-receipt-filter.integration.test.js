import { describe, afterAll, it, expect } from "vitest";

import { run, FILTER_NAME } from "../src/ses_createreceiptfilter";
import { deleteReceiptFilter } from "../src/libs/sesUtils";

describe("ses_createreceiptfilter", () => {
  afterAll(async () => {
    await deleteReceiptFilter(FILTER_NAME);
  });

  it("should successfully create a filter", async () => {
    const result = await run();
    expect(result.$metadata.httpStatusCode).toBe(200);
  });
});
