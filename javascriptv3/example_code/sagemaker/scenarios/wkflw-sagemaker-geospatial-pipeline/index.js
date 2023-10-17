/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

import { IAMClient } from "@aws-sdk/client-iam";
import { LambdaClient } from "@aws-sdk/client-lambda";
import { SageMakerClient } from "@aws-sdk/client-sagemaker";
import { SQSClient } from "@aws-sdk/client-sqs";
import { S3Client } from "@aws-sdk/client-s3";

import { Prompter } from "@aws-sdk-examples/libs/prompter.js";
import { SlowLogger } from "@aws-sdk-examples/libs/slow-logger.js";

import { SageMakerPipelinesWkflw } from "./SageMakerPipelinesWkflw.js";

const prompter = new Prompter();
const logger = new SlowLogger(25);

export async function main() {
  const pipelineWkfw = new SageMakerPipelinesWkflw(prompter, logger, {
    IAM: new IAMClient({ region: "us-west-2" }),
    Lambda: new LambdaClient({ region: "us-west-2" }),
    SageMaker: new SageMakerClient({ region: "us-west-2" }),
    S3: new S3Client({ region: "us-west-2" }),
    SQS: new SQSClient({ region: "us-west-2" }),
  });

  await pipelineWkfw.run();
}

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
