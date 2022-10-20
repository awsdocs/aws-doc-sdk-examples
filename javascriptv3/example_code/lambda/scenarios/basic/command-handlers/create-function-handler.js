/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { log } from "../../../../libs/utils/util-log.js";
import { getTmp } from "../../../../libs/utils/util-fs.js";
import { createFunction } from "../../../actions/create-function.js";

const createFunctionHandler = async (commands) => {
  const funcName = commands[1];
  const roleArn = getTmp("roleArn");

  if (!(funcName && roleArn)) {
    log(
      `Either the function name or .tmp file is missing. Did you initialize?`
    );
    return;
  }

  try {
    log(`Asking Lambda to create ${funcName}...`);
    await createFunction(funcName, roleArn);
    log(`Lambda is creating ${funcName}. Check the AWS Management Console.`);
  } catch (err) {
    log(err);
  }
};

export { createFunctionHandler };
