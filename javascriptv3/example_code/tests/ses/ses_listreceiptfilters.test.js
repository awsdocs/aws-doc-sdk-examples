import { getUniqueName } from "../../libs/index";
import { run } from "../../ses/src/ses_listreceiptfilters";
import {
  createReceiptFilter,
  deleteReceiptFilter,
} from "../../ses/src/libs/sesUtils";

describe("ses_listreceiptfilters", () => {
  const RECEIPT_FILTER_NAME = getUniqueName("ReceiptFilterName");

  beforeAll(async () => {
    await createReceiptFilter(RECEIPT_FILTER_NAME);
  });

  afterAll(async () => {
    await deleteReceiptFilter(RECEIPT_FILTER_NAME);
  });

  it("should list existing receipt filters", async () => {
    const result = await run();
    const filterMatch = result.Filters.find(
      (f) => f.Name === RECEIPT_FILTER_NAME
    );
    expect(filterMatch).toBeTruthy();
  });
});
