// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/check-workflow-status.js";
import data from "../inputs.json";

describe("test check-workflow-status", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        workflowName: `${data.inputs.workflowName}`,
        jobId: `${data.inputs.jobId}`,
      });
    },
    { timeout: 600000 },
  );
});

/*
{
        roleArn: `${data.inputs.roleArn}`,
        workflowName: `${data.inputs.workflowName}`,
        description: "Created by using the AWS SDK for JavaScript (v3).",
        inputSourceConfig: [
          {
            inputSourceARN: `${data.inputs.JSONinputSourceARN}`,
            schemaName: `${data.inputs.schemaNameJson}`,
            applyNormalization: false,
          },
          {
            inputSourceARN: `${data.inputs.CSVinputSourceARN}`,
            schemaName: `${data.inputs.schemaNameCSV}`,
            applyNormalization: false,
          },
        ],
        outputSourceConfig: [
          {
            outputS3Path: `s3://" + ${data.inputs.bucketName} + "/eroutput`,
            output: [
              {
                name: "id",
              },
              {
                name: "name",
              },
              {
                name: "email",
              },
              {
                name: "phone",
              },
            ],
            applyNormalization: false,
          },
        ],
        resolutionTechniques: { resolutionType: "ML_MATCHING" },
      }*/
