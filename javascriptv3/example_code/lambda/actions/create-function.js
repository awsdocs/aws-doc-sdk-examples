/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  Architecture,
  CreateFunctionCommand,
  LambdaClient,
  PackageType,
  Runtime,
} from "@aws-sdk/client-lambda";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";
import { zip } from "../../libs/utils/util-fs.js";
import { dirnameFromMetaUrl } from "../../libs/utils/util-fs.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/** snippet-start:[javascript.v3.lambda.actions.CreateFunction] */
const createFunction = async (funcName, roleArn) => {
  const client = createClientForDefaultRegion(LambdaClient);
  const code = await zip(`${dirname}../functions/${funcName}`);

  const command = new CreateFunctionCommand({
    Code: { ZipFile: code },
    FunctionName: funcName,
    Role: roleArn,
    Architectures: [Architecture.arm64],
    Handler: "index.handler", // Required when sending a .zip file
    PackageType: PackageType.Zip, // Required when sending a .zip file
    Runtime: Runtime.nodejs16x, // Required when sending a .zip file
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.lambda.actions.CreateFunction] */

export { createFunction };
