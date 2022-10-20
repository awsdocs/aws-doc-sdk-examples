/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { log } from "../../../../libs/utils/util-log.js";
import { updateFunctionCode } from "../../../actions/update-function-code.js";

const updateFunctionHandler = async (commands) => {
  const [_, funcName, funcCode] = commands;
  const replacementFunc = funcCode || funcName;

  try {
    log(`Updating ${funcName} with code from ${replacementFunc}...`);
    await updateFunctionCode(funcName, replacementFunc);
    log(`${funcName} updated successfully.`);
  } catch (err) {
    log(err);
  }
};

export { updateFunctionHandler };
