/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { CreateTopicCommand } from "@aws-sdk/client-sns";
import { MESSAGES } from "./messages.js";
import {
  CreateQueueCommand,
  DeleteQueueCommand,
  GetQueueAttributesCommand,
} from "@aws-sdk/client-sqs";
import { DeleteTopicCommand } from "@aws-sdk/client-sns";

// snippet-start:[javascript.v3.wkflw.sns.wrapper]
export class SNSWorkflow {
  // SNS topic is configured as First-In-First-Out
  isFifo = true;

  // Automatic content-based deduplication is enabled.
  autoDedup = true;

  snsClient;
  sqsClient;
  topicName;
  topicArn;
  /**
   * @type {{ queueName: string, queueArn: string, queueUrl: string }[]}
   */
  queues = [];
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
      this.autoDedup = await this.prompter.confirm({
        message: MESSAGES.deduplicationPrompt,
      });
    }

    this.logSeparator();
  }

  async createTopic() {
    this.topicName = await this.prompter.input({
      message: MESSAGES.topicNamePrompt,
    });
    if (this.isFifo) {
      this.topicName += ".fifo";
      await this.logger.log(MESSAGES.appendFifoNotice);
    }

    const response = await this.snsClient.send(
      new CreateTopicCommand({
        Name: this.topicName,
        Attributes: {
          FifoTopic: this.isFifo ? "true" : "false",
          ContentBasedDeduplication: this.autoDedup ? "true" : "false",
        },
      })
    );

    this.topicArn = response.TopicArn;

    await this.logger.log(
      MESSAGES.topicCreatedNotice
        .replace("${TOPIC_NAME}", this.topicName)
        .replace("${TOPIC_ARN}", this.topicArn)
    );

    this.logSeparator();
  }

  async createQueues() {
    await this.logger.log(MESSAGES.createQueuesNotice);
    let maxQueues = 2;

    for (let i = 0; i < maxQueues; i++) {
      await this.logger.log(MESSAGES.queueCount.replace("${COUNT}", i + 1));
      let queueName = await this.prompter.input({
        message: MESSAGES.queueNamePrompt,
      });

      if (this.isFifo) {
        queueName += ".fifo";
        await this.logger.log(MESSAGES.appendFifoNotice);
      }

      const response = await this.sqsClient.send(
        new CreateQueueCommand({
          QueueName: queueName,
          Attributes: { FifoQueue: this.isFifo ? "true" : "false" },
        })
      );

      const { Attributes } = await this.sqsClient.send(
        new GetQueueAttributesCommand({
          QueueUrl: response.QueueUrl,
          AttributeNames: ["QueueArn"],
        })
      );

      this.queues.push({
        queueName,
        queueArn: Attributes.QueueArn,
        queueUrl: response.QueueUrl,
      });

      await this.logger.log(
        MESSAGES.queueCreatedNotice
          .replace("${QUEUE_NAME}", queueName)
          .replace("${QUEUE_URL}", response.QueueUrl)
          .replace("${QUEUE_ARN}", Attributes.QueueArn)
      );
    }
  }

  async destroyResources() {
    if (this.queues.length) {
      for (const queue of this.queues) {
        await this.sqsClient.send(
          new DeleteQueueCommand({ QueueUrl: queue.queueUrl })
        );
      }
    }

    if (this.topicArn) {
      await this.snsClient.send(
        new DeleteTopicCommand({ TopicArn: this.topicArn })
      );
    }
  }

  async start() {
    await this.welcome();
    await this.confirmFifo();
    await this.createTopic();
    await this.createQueues();
    await this.destroyResources();
  }
}
// snippet-end:[javascript.v3.wkflw.sns.wrapper]
