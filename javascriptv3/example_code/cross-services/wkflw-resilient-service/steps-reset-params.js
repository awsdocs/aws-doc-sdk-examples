/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { ScenarioAction } from "@aws-sdk-examples/libs/scenario/scenario.js";

import { PutParameterCommand, SSMClient } from "@aws-sdk/client-ssm";
import { NAMES } from "./constants.js";

export const resetParametersSteps = [
  new ScenarioAction("baselineSsmParams", async () => {
    // These parameters are used by the python server on the EC2 instances.

    const client = new SSMClient({});
    /**
     * @type {import("@aws-sdk/client-ssm").PutParameterCommandInput[]}
     */
    const putParameterInput = [
      {
        Name: NAMES.ssmTableNameKey,
        Value: NAMES.tableName,
        Overwrite: true,
        Type: "String",
      },
      {
        Name: NAMES.ssmFailureResponseKey,
        Value: "none",
        Overwrite: true,
        Type: "String",
      },
      {
        Name: NAMES.ssmHealthCheckKey,
        Value: "shallow",
        Overwrite: true,
        Type: "String",
      },
    ];

    for (const paramInput of putParameterInput) {
      await client.send(new PutParameterCommand(paramInput));
    }
  }),
];
