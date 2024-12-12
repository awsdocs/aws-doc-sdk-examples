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
 * @param {{ confirmAll: boolean, verbose: boolean, noArt: boolean }} options
 */
export const main = async ({ confirmAll, verbose, noArt }) => {
  await ec2Scenario.run({ confirmAll, verbose, noArt });
};

// Call function if run directly
import { parseArgs } from "node:util";
import {
  isMain,
  validateArgs,
  printManPage,
} from "@aws-doc-sdk-examples/lib/utils/util-node.js";

const loadArgs = () => {
  const options = {
    help: {
      type: "boolean",
    },
    confirmAll: {
      type: "boolean",
      description: "Skip user input.",
    },
    noArt: {
      type: "boolean",
      description: "Do not display ASCII art or text decorations.",
    },
  };
  const results = parseArgs({ options });
  const { errors } = validateArgs({ options }, results);
  if (results.values.help) {
    printManPage(options, {
      name: "EC2 Basics",
      description: "Learn the basic SDK commands for Amazon EC2.",
      synopsis: "node basics.js [OPTIONS]",
    });
  }
  return { errors, results };
};

if (isMain(import.meta.url)) {
  const { errors, results } = loadArgs();
  if (errors) {
    console.error(errors.join("\n"));
  } else if (!results.values.help) {
    main(results.values);
  }
}
