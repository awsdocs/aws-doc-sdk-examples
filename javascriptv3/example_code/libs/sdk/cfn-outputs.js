// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  CloudFormationClient,
  DescribeStacksCommand,
} from "@aws-sdk/client-cloudformation";

/**
 * Get the CFN outputs for a stack.
 * @param {string} stackName
 * @param {Optional<string>} region
 * @returns {Promise<Record<string, string>}
 */
export const getCfnOutputs = async (stackName, region) => {
  const cfnClient = new CloudFormationClient({ region });

  try {
    const { Stacks } = await cfnClient.send(
      new DescribeStacksCommand({ StackName: stackName }),
    );
    if (!Stacks.length === 1) {
      throw new Error("Error getting CFN outputs. No stacks found.");
    }
    const outputs = Stacks[0].Outputs ?? [];
    return outputs.reduce((prev, { OutputKey, OutputValue }) => {
      return Object.assign({}, prev, { [OutputKey]: OutputValue });
    }, {});
  } catch (caught) {
    if (
      caught instanceof Error &&
      caught.message.includes(`Stack with id ${stackName} does not exist`)
    ) {
      const error = new Error(`Stack with id ${stackName} was not found.`);
      error.name = "StackNotFound";
      throw error;
    }
    throw caught;
  }
};
