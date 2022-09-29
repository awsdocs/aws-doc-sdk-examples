/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { log } from "../../../../libs/utils/util-log.js";
import { updateFunctionConfiguration } from "../../../actions/update-function-configuration.js";

const updateFunctionConfigurationHandler = async (commands) => {
  const [_, funcName] = commands;
  try {
    log(`Updating configuration for ${funcName}...`);
    await updateFunctionConfiguration(funcName);
    log(`Configuration for ${funcName} updated.`);
  } catch (err) {
    log(err);
  }
};

export { updateFunctionConfigurationHandler };
