/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { fileURLToPath } from "url";

// snippet-start:[sns.JavaScript.topics.getTopicAttributesV3]
import { GetTopicAttributesCommand } from "@aws-sdk/client-sns";
import { snsClient } from "../libs/snsClient.js";

/**
 * @param {string} topicArn - The ARN of the topic to retrieve attributes for.
 */
export const getTopicAttributes = async (topicArn = "TOPIC_ARN") => {
  const response = await snsClient.send(
    new GetTopicAttributesCommand({
      TopicArn: topicArn,
    })
  );
  console.log("Attributes: ", response.Attributes);
  // Attributes:  {
  //   Policy: '{...}',
  //   Owner: '...',
  //   SubscriptionsPending: '0',
  //   TopicArn: 'arn:aws:sns:us-east-1:xxxxxxxxxxxx:mytopic',
  //   TracingConfig: 'PassThrough',
  //   EffectiveDeliveryPolicy: '{"http":{"defaultHealthyRetryPolicy":{"minDelayTarget":20,"maxDelayTarget":20,"numRetries":3,"numMaxDelayRetries":0,"numNoDelayRetries":0,"numMinDelayRetries":0,"backoffFunction":"linear"},"disableSubscriptionOverrides":false,"defaultRequestPolicy":{"headerContentType":"text/plain; charset=UTF-8"}}}',
  //   SubscriptionsConfirmed: '0',
  //   DisplayName: '',
  //   SubscriptionsDeleted: '0'
  // }
  return response;
};
// snippet-end:[sns.JavaScript.topics.getTopicAttributesV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  getTopicAttributes();
}
