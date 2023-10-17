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
import { readFile } from "fs/promises";
import { dirnameFromMetaUrl } from "@aws-sdk-examples/libs/utils/util-fs.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/** snippet-start:[javascript.v3.lambda.actions.UpdateFunctionCode] */
const updateFunctionCode = async (funcName, newFunc) => {
  const client = new LambdaClient({});
  const code = await readFile(`${dirname}../functions/${newFunc}.zip`);
  const command = new UpdateFunctionCodeCommand({
    ZipFile: code,
    FunctionName: funcName,
    Architectures: [Architecture.arm64],
    Handler: "index.handler", // Required when sending a .zip file
    PackageType: PackageType.Zip, // Required when sending a .zip file
    Runtime: Runtime.nodejs16x, // Required when sending a .zip file
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.UpdateFunctionCode] */

export { updateFunctionCode };
