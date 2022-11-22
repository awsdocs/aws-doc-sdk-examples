import { describe, beforeAll, it, expect } from "vitest";

import { run, RECEIPT_FILTER_NAME } from "../src/ses_deletereceiptfilter";
import { sesClient } from "../src/libs/sesClient.js";
import {
  CreateReceiptFilterCommand,
  ListReceiptFiltersCommand,
  ReceiptFilterPolicy,
} from "@aws-sdk/client-ses";

describe("ses_deletereceiptfilter", () => {
  beforeAll(async () => {
    await createReceiptFilter(RECEIPT_FILTER_NAME);
  });

  it("should delete the receipt filter", async () => {
    let receiptFilter = await findReceiptFilter(RECEIPT_FILTER_NAME);
    expect(receiptFilter).toBeTruthy();
    await run();
    receiptFilter = await findReceiptFilter(RECEIPT_FILTER_NAME);
    expect(receiptFilter).toBeFalsy();
  });
});

function createReceiptFilter(name) {
  const createReceiptFilterCommand = new CreateReceiptFilterCommand({
    Filter: {
      Name: name,
      IpFilter: { Policy: ReceiptFilterPolicy.Block, Cidr: "10.0.0.1" },
    },
  });

  return sesClient.send(createReceiptFilterCommand);
}

async function findReceiptFilter(name) {
  const listReceiptFiltersCommand = new ListReceiptFiltersCommand({});
  const result = await sesClient.send(listReceiptFiltersCommand);
  return result.Filters.find((f) => f.Name === name);
}
