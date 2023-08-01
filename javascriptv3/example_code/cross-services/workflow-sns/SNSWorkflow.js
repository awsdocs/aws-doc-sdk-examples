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
   * @param {import('./SlowLogger.js').Logger} logger
   */
  constructor(snsClient, sqsClient, prompter, logger) {
    this.snsClient = snsClient;
    this.sqsClient = sqsClient;
    this.prompter = prompter;
    this.logger = logger;
  }

  logSeparator() {
    console.log("\n", "*".repeat(80), "\n");
  }

  async welcome() {
    await this.logger.log(MESSAGES.welcome);
    this.logSeparator();
    await this.logger.log(MESSAGES.description);
    this.logSeparator();
  }

  async confirmFifo() {
    await this.logger.log(MESSAGES.snsFifoDescription);
    this.isFifo = await this.prompter.confirm({
      message: MESSAGES.snsFifoPrompt,
    });

    if (this.isFifo) {
      this.logSeparator();
      await this.logger.log(MESSAGES.deduplicationNotice);
      await this.logger.log(MESSAGES.deduplicationDescription);
    }
  }

  async start() {
    await this.welcome();
    await this.confirmFifo();
  }
}
// snippet-end:[javascript.v3.wkflw.sns.wrapper]
