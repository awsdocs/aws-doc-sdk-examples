import {
  CreateReceiptFilterCommand,
  CreateReceiptRuleSetCommand,
  CreateTemplateCommand,
  DeleteIdentityCommand,
  DeleteReceiptFilterCommand,
  DeleteReceiptRuleSetCommand,
  DeleteTemplateCommand,
  DescribeReceiptRuleCommand,
  GetTemplateCommand,
  ListIdentitiesCommand,
  ListReceiptFiltersCommand,
  ListReceiptRuleSetsCommand,
  ReceiptFilterPolicy,
  VerifyEmailIdentityCommand,
} from "@aws-sdk/client-ses";
import { sesClient } from "./sesClient";

export async function checkRuleExists(ruleName, ruleSetName) {
  const describeReceiptRuleCommand = new DescribeReceiptRuleCommand({
    RuleName: ruleName,
    RuleSetName: ruleSetName,
  });

  try {
    await sesClient.send(describeReceiptRuleCommand);
    return true;
  } catch {
    return false;
  }
}

export function createIdentity(identityName) {
  const verifyIdentityCommand = new VerifyEmailIdentityCommand({
    EmailAddress: identityName,
  });
  return sesClient.send(verifyIdentityCommand);
}

export function createReceiptFilter(receiptFilterName) {
  const createReceiptFilterCommand = new CreateReceiptFilterCommand({
    Filter: {
      Name: receiptFilterName,
      IpFilter: { Policy: ReceiptFilterPolicy.Block, Cidr: "10.0.0.1" },
    },
  });

  return sesClient.send(createReceiptFilterCommand);
}

export function createReceiptRuleSet(ruleSetName) {
  const createRuleSetCommand = new CreateReceiptRuleSetCommand({
    RuleSetName: ruleSetName,
  });
  return sesClient.send(createRuleSetCommand);
}

export function createTemplate(templateName) {
  const createTemplateCommand = new CreateTemplateCommand({
    Template: {
      TemplateName: templateName,
      HtmlPart: "<h1>Hello, {{name}}!</h1><p>Don't forget the party gifts.</p>",
      SubjectPart: "SUBJECT",
      TextPart: "TEXT_CONTENT",
    },
  });

  return sesClient.send(createTemplateCommand);
}

export function deleteIdentity(identityName) {
  const deleteIdentityCommand = new DeleteIdentityCommand({
    Identity: identityName,
  });

  return sesClient.send(deleteIdentityCommand);
}

export function deleteReceiptFilter(receiptFilterName) {
  const deleteReceiptFilterCommand = new DeleteReceiptFilterCommand({
    FilterName: receiptFilterName,
  });

  return sesClient.send(deleteReceiptFilterCommand);
}

export function deleteReceiptRuleSet(ruleSetName) {
  const deleteRuleSetCommand = new DeleteReceiptRuleSetCommand({
    RuleSetName: ruleSetName,
  });

  return sesClient.send(deleteRuleSetCommand);
}

export function deleteTemplate(templateName) {
  const deleteTemplateCommand = new DeleteTemplateCommand({
    TemplateName: templateName,
  });
  return sesClient.send(deleteTemplateCommand);
}

export async function findIdentity(identityName) {
  const listIdentitiesCommand = new ListIdentitiesCommand({});
  const result = await sesClient.send(listIdentitiesCommand);
  return result.Identities.find((id) => id === identityName);
}

export async function findReceiptFilter(receiptFilterName) {
  const listReceiptFiltersCommand = new ListReceiptFiltersCommand({});
  const result = await sesClient.send(listReceiptFiltersCommand);
  return result.Filters.find(f => f.Name === receiptFilterName);
}

export async function findReceiptRuleSet(name) {
  const listReceiptRuleSetsCommand = new ListReceiptRuleSetsCommand({});
  const result = await sesClient.send(listReceiptRuleSetsCommand);
  return result.RuleSets.find((rs) => {
    return rs.Name === name;
  });
}

export async function getTemplate(templateName) {
  const getTemplateCommand = new GetTemplateCommand({
    TemplateName: templateName,
  });

  try {
    return await sesClient.send(getTemplateCommand);
  } catch (e) {
    return null;
  }
}
