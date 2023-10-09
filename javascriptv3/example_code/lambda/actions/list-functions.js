/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { LambdaClient, ListFunctionsCommand } from "@aws-sdk/client-lambda";

/** snippet-start:[javascript.v3.lambda.actions.ListFunctions] **/
const listFunctions = () => {
  const client = new LambdaClient({});
  const command = new ListFunctionsCommand({});

  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.ListFunctions] */

export { listFunctions };
