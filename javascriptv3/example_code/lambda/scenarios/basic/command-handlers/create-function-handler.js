// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { getTmp } from "@aws-doc-sdk-examples/lib/utils/util-fs.js";
import { createFunction } from "../../../actions/create-function.js";

/**
 * @param {string[]} commands
 */
const createFunctionHandler = async (commands) => {
  const funcName = commands[1];
  const roleArn = getTmp("roleArn");

  if (!(funcName && roleArn)) {
    logger.log(
      "Either the function name or .tmp file is missing. Did you initialize?",
    );
    return;
  }

  try {
    logger.log(`Asking Lambda to create ${funcName}...`);
    await createFunction(funcName, roleArn);
    logger.log(
      `Lambda is creating ${funcName}. Check the AWS Management Console.`,
    );
  } catch (err) {
    logger.error(err);
  }
};

export { createFunctionHandler };
