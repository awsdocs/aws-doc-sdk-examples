/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { type } from "ramda";
import { log } from "../../../../libs/utils/util-log.js";
import { invoke } from "../../../actions/invoke.js";

const invokeHandler = async (commands) => {
  const [_, funcName, ...funcArgs] = commands;

  if (!funcName) {
    log(
      `Function name is missing. It must be provided as an argument to the 'invoke' command.`
    );
    return;
  }

  try {
    log(
      `Invoking ${funcName} with ${type(funcArgs)}(${JSON.stringify(
        funcArgs
      )})...`
    );
    const { logs, result } = await invoke(funcName, funcArgs);
    log(`${funcName} invoked successfully. Result was: ${result}`);
    log(`Logs: ${logs}`);
  } catch (err) {
    log(err);
  }
};

export { invokeHandler };
