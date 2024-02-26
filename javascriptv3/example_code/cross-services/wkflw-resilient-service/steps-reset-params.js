// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { ScenarioAction } from "@aws-doc-sdk-examples/lib/scenario/scenario.js";

import { PutParameterCommand, SSMClient } from "@aws-sdk/client-ssm";
import { NAMES } from "./constants.js";

export const initParamsSteps = [
  new ScenarioAction("initParams", async () => {
    // These parameters are used by the python server on the EC2 instances.

    const client = new SSMClient({});
    /**
     * @type {import("@aws-sdk/client-ssm").PutParameterCommandInput[]}
     */
    const putParameterInput = [
      tableNameParam,
      noFailureResponseParam,
      shallowHealthCheckParam,
    ];

    for (const paramInput of putParameterInput) {
      await client.send(new PutParameterCommand(paramInput));
    }
  }),
];

export const tableNameParam = {
  Name: NAMES.ssmTableNameKey,
  Value: NAMES.tableName,
  Overwrite: true,
  Type: "String",
};

export const noFailureResponseParam = {
  Name: NAMES.ssmFailureResponseKey,
  Value: "none",
  Overwrite: true,
  Type: "String",
};

export const shallowHealthCheckParam = {
  Name: NAMES.ssmHealthCheckKey,
  Value: "shallow",
  Overwrite: true,
  Type: "String",
};
