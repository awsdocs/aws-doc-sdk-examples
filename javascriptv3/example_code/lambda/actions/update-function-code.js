/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  LambdaClient,
  UpdateFunctionCodeCommand,
  PackageType,
  Runtime,
  Architecture,
} from "@aws-sdk/client-lambda";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";
import { zip, dirnameFromMetaUrl } from "../../libs/utils/util-fs.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/** snippet-start:[javascript.v3.lambda.actions.UpdateFunctionCode] */
const updateFunctionCode = async (funcName, newFunc) => {
  const client = createClientForDefaultRegion(LambdaClient);
  const code = await zip(`${dirname}../functions/${newFunc}`);
  const command = new UpdateFunctionCodeCommand({
    ZipFile: code,
    FunctionName: funcName,
    Architectures: [Architecture.arm64],
    Handler: "index.handler", // Required when sending a Zip
    PackageType: PackageType.Zip, // Required when sending a Zip
    Runtime: Runtime.nodejs16x, // Required when sending a Zip
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.UpdateFunctionCode] */

export { updateFunctionCode };
