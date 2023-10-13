/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { LambdaClient, DeleteFunctionCommand } from "@aws-sdk/client-lambda";

/** snippet-start:[javascript.v3.lambda.actions.DeleteFunction] */
/**
 * @param {string} funcName
 */
const deleteFunction = (funcName) => {
  const client = new LambdaClient({});
  const command = new DeleteFunctionCommand({ FunctionName: funcName });
  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.DeleteFunction] */

export { deleteFunction };
