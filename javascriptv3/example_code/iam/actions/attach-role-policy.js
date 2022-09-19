import { IAMClient, AttachRolePolicyCommand } from "@aws-sdk/client-iam";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

const attachRolePolicy = async (roleName, policyArn) => {
  /* snippet-start:[javascript.v3.iam.actions.AttachRolePolicy] */
  const client = createClientForDefaultRegion(IAMClient);
  const command = new AttachRolePolicyCommand({
    PolicyArn: policyArn, // e.g. arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    RoleName: roleName, // e.g. lambda-basic-execution-role
  });
  /* snippet-end:[javascript.v3.iam.actions.AttachRolePolicy] */

  return client.send(command);
};

export { attachRolePolicy };
