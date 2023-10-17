/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, test } from "vitest";
import { SageMakerPipelinesWkflw } from "../scenarios/wkflw-sagemaker-geospatial-pipeline/SageMakerPipelinesWkflw.js";
import { Logger } from "@aws-sdk-examples/libs/logger.js";
import { IAMClient } from "@aws-sdk/client-iam";
import { SageMakerClient } from "@aws-sdk/client-sagemaker";
import { S3Client } from "@aws-sdk/client-s3";
import { LambdaClient } from "@aws-sdk/client-lambda";
import { SQSClient } from "@aws-sdk/client-sqs";

describe("SageMaker geospatial pipeline workflow", () => {
  test("should execute successfully", async () => {
    const prompter = {
      checkContinue() {
        return Promise.resolve(true);
      },
      confirm() {
        return Promise.resolve(true);
      },
      logSeparator() {},
    };

    const logger = new Logger();

    const clients = {
      IAM: new IAMClient({ region: "us-west-2" }),
      SageMaker: new SageMakerClient({ region: "us-west-2" }),
      S3: new S3Client({ region: "us-west-2" }),
      Lambda: new LambdaClient({ region: "us-west-2" }),
      SQS: new SQSClient({ region: "us-west-2" }),
    };

    const wkflw = new SageMakerPipelinesWkflw(prompter, logger, clients);

    await wkflw.run();
  });
});
