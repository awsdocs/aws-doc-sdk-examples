// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { EC2Client } from "@aws-sdk/client-ec2";

import { Scenario } from "@aws-doc-sdk-examples/lib/scenario/index.js";
import {
  greeting,
  createKeyPair,
  provideKeyPairName,
  logKeyPair,
  confirmDeleteKeyPair,
  maybeDeleteKeyPair,
  createSecurityGroup,
  logSecurityGroup,
  authorizeSecurityGroupIngress,
  logSecurityGroupIngress,
  provideSecurityGroupName,
  getImages,
  getCompatibleInstanceTypes,
  provideInstanceType,
  logRunInstance,
  runInstance,
  logSSHConnectionInfo,
  logIpAddressBehavior,
  logStopInstance,
  stopInstance,
  confirmTerminateInstance,
  maybeTerminateInstance,
  confirmDeleteSecurityGroup,
  maybeDeleteSecurityGroup,
  confirm,
  exitOnNoConfirm,
  provideImage,
  logErrors,
  describeInstance,
  logStartInstance,
  startInstance,
  logCleanUp,
  logIpAllocation,
  allocateIp,
  associateIp,
  logStaticIpProof,
  confirmDisassociateAddress,
  maybeDisassociateAddress,
  maybeReleaseAddress,
  deleteTemporaryDirectory,
} from "./steps.js";

export const ec2Scenario = new Scenario(
  "EC2",
  [
    greeting,
    confirm,
    exitOnNoConfirm,
    provideKeyPairName,
    createKeyPair,
    logKeyPair,
    provideSecurityGroupName,
    createSecurityGroup,
    logSecurityGroup,
    authorizeSecurityGroupIngress,
    logSecurityGroupIngress,
    getImages,
    provideImage,
    getCompatibleInstanceTypes,
    provideInstanceType,
    logRunInstance,
    confirm,
    exitOnNoConfirm,
    runInstance,
    describeInstance,
    logSSHConnectionInfo,
    confirm,
    exitOnNoConfirm,
    logIpAddressBehavior,
    confirm,
    exitOnNoConfirm,
    logStopInstance,
    stopInstance,
    logStartInstance,
    startInstance,
    describeInstance,
    logSSHConnectionInfo,
    confirm,
    exitOnNoConfirm,
    logIpAllocation,
    confirm,
    exitOnNoConfirm,
    allocateIp,
    associateIp,
    logSSHConnectionInfo,
    confirm,
    exitOnNoConfirm,
    logStaticIpProof,
    confirm,
    exitOnNoConfirm,
    logStopInstance,
    stopInstance,
    logStartInstance,
    startInstance,
    logSSHConnectionInfo,
    logCleanUp,
    confirm,
    exitOnNoConfirm,
    confirmDisassociateAddress,
    maybeDisassociateAddress,
    maybeReleaseAddress,
    confirmTerminateInstance,
    maybeTerminateInstance,
    confirmDeleteSecurityGroup,
    maybeDeleteSecurityGroup,
    confirmDeleteKeyPair,
    maybeDeleteKeyPair,
    deleteTemporaryDirectory,
    logErrors,
  ],
  { ec2Client: new EC2Client({}), errors: [] },
);

/**
 * Run the EC2 introductory scenario. This will make changes
 * in your AWS account.
 * @param {{ confirmAll: boolean, verbose: boolean }} options
 */
export const main = async ({ confirmAll, verbose }) => {
  await ec2Scenario.run({ confirmAll, verbose });
};

// Call function if run directly
import { fileURLToPath } from "node:url";
import { parseArgs } from "node:util";
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const { values } = parseArgs({
    options: {
      yes: {
        type: "boolean",
        short: "y",
      },
    },
  });
  main({ confirmAll: values.yes });
}
