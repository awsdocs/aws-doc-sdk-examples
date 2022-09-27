import { IAMClient, CreateRoleCommand } from "@aws-sdk/client-iam";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

const createRole = async (createRoleCommandInput) => {
  const client = createClientForDefaultRegion(IAMClient);

  /**
   * Example CreateRoleCommandInput:
   * 
   * {
          Version: "2012-10-17",
          Statement: [
            {
              Effect: "Allow",
              Principal: {
                Service: "lambda.amazonaws.com",
              },
              Action: "sts:AssumeRole",
            },
          ],
        }),
        RoleName: roleName,
      }
   */
  const command = new CreateRoleCommand(createRoleCommandInput);

  return client.send(command);
};

export { createRole };
