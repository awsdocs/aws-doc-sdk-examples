/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  EC2Client,
  RunInstancesCommand,
  TerminateInstancesCommand,
} from "@aws-sdk/client-ec2";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

const client = new EC2Client({ region: DEFAULT_REGION });

export const runEC2Instance = async () => {
  // Free Tier - Amazon Linux 2 AMI (HVM) - Kernel 5.10, SSD Volume Type
  const imageId = "ami-0b0dcb5067f052a63";
  const command = new RunInstancesCommand({
    MinCount: 1,
    MaxCount: 1,
    ImageId: imageId,
  });
  const { Instances } = await client.send(command);
  return Instances[0].InstanceId;
};

/**
 *
 * @param {string} instanceId
 */
export const terminateEC2Instance = async (instanceId) => {
  const command = new TerminateInstancesCommand({ InstanceIds: [instanceId] });
  return client.send(command);
};
