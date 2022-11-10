/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { log } from "../log.js";

const envValid = (envVars) => envVars.every((envVar) => !!envVar);

const validateEnv = async (context) => {
  log("Checking if environment variables exist.");

  if (!context || !context.envVars) {
    throw new Error("Missing context.");
  }

  if (!envValid(Object.values(context.envVars))) {
    throw new Error(
      "Missing environment variables. Did you run the setup steps in the readme?"
    );
  }

  log("Variables exist.", { type: "success" });
  return { ...context };
};

export { validateEnv };
