/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { LambdaClient, GetFunctionCommand } from "@aws-sdk/client-lambda";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.lambda.actions.GetFunction] */
const getFunction = (funcName) => {
  const client = createClientForDefaultRegion(LambdaClient);
  const command = new GetFunctionCommand({ FunctionName: funcName });
  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.GetFunction] */

export { getFunction };
