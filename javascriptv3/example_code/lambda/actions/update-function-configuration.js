/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  LambdaClient,
  UpdateFunctionConfigurationCommand,
} from "@aws-sdk/client-lambda";
import { readFileSync } from "fs";
import { dirnameFromMetaUrl } from "@aws-sdk-examples/libs/utils/util-fs.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/** snippet-start:[javascript.v3.lambda.actions.UpdateFunctionConfiguration] */
const updateFunctionConfiguration = (funcName) => {
  const client = new LambdaClient({});
  const config = readFileSync(`${dirname}../functions/config.json`).toString();
  const command = new UpdateFunctionConfigurationCommand({
    ...JSON.parse(config),
    FunctionName: funcName,
  });
  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.UpdateFunctionConfiguration] */

export { updateFunctionConfiguration };
