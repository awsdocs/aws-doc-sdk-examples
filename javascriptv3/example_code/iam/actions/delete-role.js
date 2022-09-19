import { DeleteRoleCommand, IAMClient } from "@aws-sdk/client-iam";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

const deleteRole = (roleName) => {
  /* snippet-start:[javascript.v3.iam.actions.DeleteRole] */
  const client = createClientForDefaultRegion(IAMClient);
  const command = new DeleteRoleCommand({ RoleName: roleName });
  return client.send(command);
  /* snippet-end:[javascript.v3.iam.actions.DeleteRole] */
};

export { deleteRole };
