// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { invoke } from "../../../actions/invoke.js";

const invokeHandler = async (commands) => {
  const [_, funcName, ...funcArgs] = commands;

  if (!funcName) {
    logger.log(
      `Function name is missing. It must be provided as an argument to the 'invoke' command.`,
    );
    return;
  }

  try {
    logger.log(
      `Invoking ${funcName} with ${typeof funcArgs}(${JSON.stringify(
        funcArgs,
      )})...`,
    );
    const { logs, result } = await invoke(funcName, funcArgs);
    logger.log(`${funcName} invoked successfully. Result was: ${result}`);
    logger.log(`Logs: ${logs}`);
  } catch (err) {
    logger.error(err);
  }
};

export { invokeHandler };
