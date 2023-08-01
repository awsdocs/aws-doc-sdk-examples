/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[javascript.v3.wkflw.sns.index]
import { SNSClient } from "@aws-sdk/client-sns";
import { SQSClient } from "@aws-sdk/client-sqs";

import { SNSWorkflow } from "./SNSWorkflow.js";
import { Prompter } from "./Prompter.js";
import { SlowLogger } from "./SlowLogger.js";

export const startSnsWorkflow = () => {
  const snsClient = new SNSClient({});
  const sqsClient = new SQSClient({});
  const prompter = new Prompter();
  const logger = new SlowLogger(25);

  const wkflw = new SNSWorkflow(snsClient, sqsClient, prompter, logger);

  wkflw.start();
};

// snippet-end:[javascript.v3.wkflw.sns.index]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  startSnsWorkflow();
}
