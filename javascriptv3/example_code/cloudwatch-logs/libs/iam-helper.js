/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  CreateRoleCommand,
  DeleteRoleCommand,
  IAMClient,
} from "@aws-sdk/client-iam";
import { DEFAULT_REGION } from "./constants.js";

const client = new IAMClient({ region: DEFAULT_REGION });

export const createRole = async (roleName) => {
  const command = new CreateRoleCommand({
    AssumeRolePolicyDocument: JSON.stringify({
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
  });

  const {
    Role: { Arn },
  } = await client.send(command);

  return Arn;
};

export const deleteRole = (roleName) => {
  const command = new DeleteRoleCommand({ RoleName: roleName });
  return client.send(command);
};
