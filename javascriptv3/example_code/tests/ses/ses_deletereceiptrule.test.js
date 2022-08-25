import { CreateReceiptRuleCommand, TlsPolicy } from "@aws-sdk/client-ses";
import { sesClient } from "../../ses/src/libs/sesClient";
import {
  createReceiptRuleSet,
  checkRuleExists,
  deleteReceiptRuleSet,
} from "../../ses/src/libs/sesUtils";
import {
  RULE_NAME,
  RULE_SET_NAME,
  run,
} from "../../ses/src/ses_deletereceiptrule";

describe("ses_deletereceiptrule", () => {
  beforeAll(async () => {
    try {
      await createReceiptRuleSet(RULE_SET_NAME);
      await createReceiptRule(RULE_NAME, RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(() => {
    try {
      deleteReceiptRuleSet(RULE_SET_NAME);
    } catch(e) {
      console.error(e);
    }
  })

  it("should delete a receipt rule", async () => {
    let rule = await checkRuleExists(RULE_NAME, RULE_SET_NAME);
    expect(rule).toBe(true);
    await run();
    rule = await checkRuleExists(RULE_NAME, RULE_SET_NAME);
    expect(rule).toBe(false);
  });
});

function createReceiptRule(ruleName, ruleSetName) {
  const createReceiptRuleCommand = new CreateReceiptRuleCommand({
    Rule: {
      Actions: [
        {
          AddHeaderAction: {
            HeaderName: "Test-Header",
            HeaderValue: "Test-Value",
          },
        },
      ],
      Recipients: ["fake@example.com"],
      Enabled: true,
      Name: ruleName,
      ScanEnabled: false,
      TlsPolicy: TlsPolicy.Optional,
    },
    RuleSetName: ruleSetName,
  });

  return sesClient.send(createReceiptRuleCommand);
}
