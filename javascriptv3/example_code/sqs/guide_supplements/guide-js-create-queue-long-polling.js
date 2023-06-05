/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * This example supplements the following guide. For more context, see
 * https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html
 */

import { fileURLToPath } from "url";

// snippet-start:[sqs.JavaScript.longPoll.createQueueV3]
import { CreateQueueCommand, SQSClient } from "@aws-sdk/client-sqs";

const client = new SQSClient({});
const SQS_QUEUE_NAME = "queue_name";

export const main = async (queueName = SQS_QUEUE_NAME) => {
  const response = await client.send(
    new CreateQueueCommand({
      QueueName: queueName,
      Attributes: {
        ReceiveMessageWaitTimeSeconds: "20",
      },
    })
  );
  console.log(response);
  return response;
};
// snippet-end:[sqs.JavaScript.longPoll.createQueueV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
