// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { updateFunctionConfiguration } from "../../../actions/update-function-configuration.js";

const updateFunctionConfigurationHandler = async (commands) => {
  const [_, funcName] = commands;
  try {
    logger.log(`Updating configuration for ${funcName}...`);
    await updateFunctionConfiguration(funcName);
    logger.log(`Configuration for ${funcName} updated.`);
  } catch (err) {
    logger.error(err);
  }
};

export { updateFunctionConfigurationHandler };
