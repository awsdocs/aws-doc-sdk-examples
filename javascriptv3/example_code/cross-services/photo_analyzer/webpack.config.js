// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { resolve } from "node:path";

import webpack from "webpack";
import { getCfnOutputs } from "@aws-doc-sdk-examples/lib/sdk/cfn-outputs.js";

import { stackName } from "./src/constants.js";

const defaultRegion = "us-east-1";
const outputs = await getCfnOutputs(
  stackName,
  process.env.REGION ?? defaultRegion,
);

export default {
  entry: "./src/index.js",
  output: {
    filename: "main.js",
    path: resolve(import.meta.dirname, "dist"),
  },
  plugins: [
    new webpack.EnvironmentPlugin({
      VERIFIED_EMAIL_ADDRESS: "",
      REGION: defaultRegion,
      CFN_OUTPUTS: JSON.stringify(outputs),
    }),
  ],
};
