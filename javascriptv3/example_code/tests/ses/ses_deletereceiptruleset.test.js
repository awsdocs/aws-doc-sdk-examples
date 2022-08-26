import { run, RULE_SET_NAME } from "../../ses/src/ses_deletereceiptruleset";
import {
  createReceiptRuleSet,
  findReceiptRuleSet,
} from "../../ses/src/libs/sesUtils";

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
