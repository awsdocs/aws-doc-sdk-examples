// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { updateFunctionCode } from "../../../actions/update-function-code.js";

const updateFunctionHandler = async (commands) => {
  const [_, funcName, funcCode] = commands;
  const replacementFunc = funcCode || funcName;

  try {
    logger.log(`Updating ${funcName} with code from ${replacementFunc}...`);
    await updateFunctionCode(funcName, replacementFunc);
    logger.log(`${funcName} updated successfully.`);
  } catch (err) {
    logger.error(err);
  }
};

export { updateFunctionHandler };
