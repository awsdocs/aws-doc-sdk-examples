/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  LambdaClient,
  waitUntilFunctionActive,
  waitUntilFunctionExists,
  waitUntilFunctionUpdated,
} from "@aws-sdk/client-lambda";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk";

const MAX_WAIT_TIME = 60;

const waitForFunction = (getFunctionCommandInput) =>
  waitUntilFunctionExists(
    {
      client: createClientForDefaultRegion(LambdaClient),
      maxWaitTime: MAX_WAIT_TIME,
    },
    getFunctionCommandInput
  );

const waitForFunctionActive = (getFunctionConfigurationCommandInput) =>
  waitUntilFunctionActive(
    {
      client: createClientForDefaultRegion(LambdaClient),
      maxWaitTime: MAX_WAIT_TIME,
    },
    getFunctionConfigurationCommandInput
  );

const waitForFunctionUpdated = (getFunctionCommandInput) =>
  waitUntilFunctionUpdated(
    {
      client: createClientForDefaultRegion(LambdaClient),
      maxWaitTime: MAX_WAIT_TIME,
    },
    getFunctionCommandInput
  );

export { waitForFunction, waitForFunctionActive, waitForFunctionUpdated };
