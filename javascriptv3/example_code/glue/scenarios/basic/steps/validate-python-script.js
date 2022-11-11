/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

const findPythonScript = async (s3ListObjects, bucketName) => {
  try {
    const { Contents } = await s3ListObjects(bucketName);
    const script = Contents.find(
      (obj) => obj.Key === process.env.PYTHON_SCRIPT_KEY
    );
    return !!script;
  } catch {
    return false;
  }
};

const makeValidatePythonScriptStep =
  ({ s3ListObjects }) =>
  async (context) => {
    log("Checking if ETL python script exists.");
    const scriptExists = await findPythonScript(
      s3ListObjects,
      process.env.BUCKET_NAME
    );

    if (scriptExists) {
      log("ETL python script exists.", { type: "success" });
      return { ...context };
    } else {
      throw new Error(
        "Missing ETL python script. Did you run the setup steps in the readme?"
      );
    }
  };

export { makeValidatePythonScriptStep };
