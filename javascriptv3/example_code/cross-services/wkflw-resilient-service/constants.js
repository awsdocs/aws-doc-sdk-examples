/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { resolve, join } from "node:path";

export const PROJECT_ROOT_PATH = resolve("../../../../");
export const RESOURCES_PATH = join(
  PROJECT_ROOT_PATH,
  "workflows/resilient_service/resources/",
);

export const MESSAGES = {
  introduction: `Welcome to the demonstration of How to Build and Manage a Resilient Service!
For this demo, we'll use an AWS SDK to create several AWS resources to set up a load-balanced web service endpoint and explore some ways to make it resilient against various kinds of failures.
Some of the resources create by this demo are:
  - A DynamoDB table that the web service depends on to provide book, movie, and song recommendations.
  - An EC2 launch template that defines EC2 instances that each contain a Python web server.
  - An EC2 Auto Scaling group that manages EC2 instances across several Availability Zones.
  - An Elastic Load Balancing (ELB) load balancer that targets the Auto Scaling group to distribute requests.`,
  confirmDeployment: "Are you ready to deploy resources?",
  creatingTable: "Creating table: ${TABLE_NAME}.",
  createdTable: "Created table: ${TABLE_NAME}.",
  populatingTable: "Populating table: ${TABLE_NAME}.",
  populatedTable: "Populated table: ${TABLE_NAME}.",
  creatingKeyPair:
    "Creating key pair: ${KEY_PAIR_NAME}. This allows you to SSH into the EC2 instances.",
  createdKeyPair:
    "Created key pair: ${KEY_PAIR_NAME}.pem and stored it locally.",
  creatingInstancePolicy:
    "Creating instance policy: ${INSTANCE_POLICY_NAME}. The policy is added to an instance profile.",
  createdInstancePolicy:
    "Instance policy created: ${INSTANCE_POLICY_NAME}. ARN: ${INSTANCE_POLICY_ARN}.",
  creatingInstanceProfile:
    "Creating instance profile: ${INSTANCE_PROFILE_NAME}. ",
  destroy: "Destroy resources?",
  deletedTable: "Deleted table: ${TABLE_NAME}.",
  deleteTableError: "Error deleting table: ${TABLE_NAME}.",
  deletedKeyPair: "Deleted key pair ${KEY_PAIR_NAME}.",
  deleteKeyPairError: "Error deleting key pair: ${KEY_PAIR_NAME}.",
  deletedPolicy: "Deleted policy ${INSTANCE_POLICY_NAME}.",
  deletePolicyError: "Error deleting policy ${INSTANCE_POLICY_NAME}.",
};

export const PREFIX = "resilient-wkflw-";

export const NAMES = {
  tableName: `${PREFIX}table`,
  keyPairName: `${PREFIX}key-pair`,
  instancePolicyName: `${PREFIX}instance-policy`,
  instanceProfileName: `${PREFIX}instance-profile`,
};
