/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { MESSAGES } from "./messages.js";

// snippet-start:[javascript.v3.wkflw.sns.wrapper]
export class SNSWorkflow {
  // SNS topic is configured as First-In-First-Out
  isFifo = true;

  // Automatic content-based deduplication is enabled.
  autoDedup = true;

  snsClient;
  sqsClient;
  prompter;

  /**
   * @param {import('@aws-sdk/client-sns').SNSClient} snsClient
   * @param {import('@aws-sdk/client-sqs').SQSClient} sqsClient
   * @param {import('./Prompter.js').Prompter} prompter
   */
  constructor(snsClient, sqsClient, prompter) {
    this.snsClient = snsClient;
    this.sqsClient = sqsClient;
    this.prompter = prompter;
  }

  logSeparator() {
    console.log("\n", "*".repeat(80), "\n");
  }

  async start() {
    // WELCOME
    console.log(MESSAGES.welcome);
    this.logSeparator();
    console.log(MESSAGES.description);
    this.logSeparator();

    // USE FIFO?
    console.log(MESSAGES.snsFifoDescription);
    this.isFifo = await this.prompter.confirm({
      message: MESSAGES.snsFifoPrompt,
    });
  }
}
// snippet-end:[javascript.v3.wkflw.sns.wrapper]
