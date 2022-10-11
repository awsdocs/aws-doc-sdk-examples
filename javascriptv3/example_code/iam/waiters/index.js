/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { IAMClient, waitUntilRoleExists } from "@aws-sdk/client-iam";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

const MAX_WAIT_TIME = 15;

const waitForRole = (getRoleCommandInput) =>
  waitUntilRoleExists(
    {
      client: createClientForDefaultRegion(IAMClient),
      maxWaitTime: MAX_WAIT_TIME,
    },
    getRoleCommandInput
  );

export { waitForRole };
