import { RULE_SET_NAME, run } from "../../ses/src/ses_createreceiptruleset";
import {
  deleteReceiptRuleSet,
  findReceiptRuleSet,
} from "../../ses/src/libs/sesUtils";

describe("ses_createreceiptruleset", () => {
  afterAll(async () => {
    try {
      await deleteReceiptRuleSet(RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should successfully create a ruleset", async () => {
    await run();
    const createdRuleSet = await findReceiptRuleSet(RULE_SET_NAME);
    expect(createdRuleSet).toBeTruthy();
  });
});
