/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  AttachRolePolicyCommand,
  CreateRoleCommand,
  DeletePolicyCommand,
  DeleteRoleCommand,
  DetachRolePolicyCommand,
  IAMClient,
} from "@aws-sdk/client-iam";
import { DEFAULT_REGION } from "./constants.js";

const client = new IAMClient({ region: DEFAULT_REGION });

const createRole = async (roleName, statement) => {
  const command = new CreateRoleCommand({
    AssumeRolePolicyDocument: JSON.stringify({
      Version: "2012-10-17",
      Statement: statement,
    }),
    RoleName: roleName,
  });

  const {
    Role: { Arn },
  } = await client.send(command);

  return Arn;
};

export const createLambdaRole = (roleName) => {
  return createRole(roleName, [
    {
      Effect: "Allow",
      Principal: {
        Service: "lambda.amazonaws.com",
      },
      Action: "sts:AssumeRole",
    },
  ]);
};

export const deleteRole = (roleName) => {
  const command = new DeleteRoleCommand({ RoleName: roleName });
  return client.send(command);
};

export const attachRolePolicy = (roleName, policyArn) => {
  const command = new AttachRolePolicyCommand({
    PolicyArn: policyArn, // For example, arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    RoleName: roleName, // For example, lambda-basic-execution-role
  });

  return client.send(command);
};

export const detachRolePolicy = (roleName, policyArn) => {
  const command = new DetachRolePolicyCommand({
    PolicyArn: policyArn,
    RoleName: roleName,
  });
  return client.send(command);
};

export const deletePolicy = (policyArn) => {
  const command = new DeletePolicyCommand({ PolicyArn: policyArn });
  return client.send(command);
};
