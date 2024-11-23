// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  LambdaClient,
  UpdateFunctionConfigurationCommand,
  waitUntilFunctionUpdated,
} from "@aws-sdk/client-lambda";
import { readFileSync } from "node:fs";
import { dirnameFromMetaUrl } from "@aws-doc-sdk-examples/lib/utils/util-fs.js";
import { waitForFunctionUpdated } from "../waiters/index.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/** snippet-start:[javascript.v3.lambda.actions.UpdateFunctionConfiguration] */
const updateFunctionConfiguration = (funcName) => {
  const client = new LambdaClient({});
  const config = readFileSync(`${dirname}../functions/config.json`).toString();
  const command = new UpdateFunctionConfigurationCommand({
    ...JSON.parse(config),
    FunctionName: funcName,
  });
  const result = client.send(command);
  waitForFunctionUpdated({ FunctionName: funcName });
  return result;
};
/** snippet-end:[javascript.v3.lambda.actions.UpdateFunctionConfiguration] */

export { updateFunctionConfiguration };
