import { describe, beforeAll, it, expect } from "vitest";

import { run, RULE_SET_NAME } from "../src/ses_deletereceiptruleset";
import { createReceiptRuleSet, findReceiptRuleSet } from "../src/libs/sesUtils";

describe("ses_deletereceiptruleset", () => {
  beforeAll(async () => {
    await createReceiptRuleSet(RULE_SET_NAME);
  });

  it("should successfully delete a receipt rule set", async () => {
    let ruleSet = await findReceiptRuleSet(RULE_SET_NAME);
    expect(ruleSet).toBeTruthy();
    await run();
    ruleSet = await findReceiptRuleSet(RULE_SET_NAME);
    expect(ruleSet).toBeFalsy();
  });
});
