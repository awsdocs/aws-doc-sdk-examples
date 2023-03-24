// snippet-start:[iam.JavaScript.policies.detachRolePolicyV3]
import { DetachRolePolicyCommand, IAMClient } from "@aws-sdk/client-iam";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

const detachRolePolicy = (roleName, policyArn) => {
  const client = createClientForDefaultRegion(IAMClient);
  const command = new DetachRolePolicyCommand({
    PolicyArn: policyArn,
    RoleName: roleName,
  });
  return client.send(command);
};
// snippet-end:[iam.JavaScript.policies.detachRolePolicyV3]

export { detachRolePolicy };
