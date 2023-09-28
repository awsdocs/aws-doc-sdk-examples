import { describe, it, expect } from "vitest";
import { paginateListTopics } from "@aws-sdk/client-sns";

import { createTopic } from "../actions/create-topic.js";
import { deleteTopic } from "../actions/delete-topic.js";
import { snsClient } from "../libs/snsClient.js";
import { getTopicAttributes } from "../actions/get-topic-attributes.js";

describe("createTopic/deleteTopic", () => {
  let topicArn = "";
  const topicName = "createDeleteSnsTopicTest";

  it("should create and delete a topic", async () => {
    // Create topic.
    const { TopicArn } = await createTopic(topicName);
    topicArn = TopicArn;
    expect(TopicArn).toBeDefined();

    // Get topic attributes.
    const response = await getTopicAttributes(topicArn);
    expect(response.Attributes.TopicArn).toEqual(topicArn);

    // Delete topic.
    await deleteTopic(topicArn);

    let foundTopic = false;
    const paginator = paginateListTopics(
      {
        client: snsClient,
      },
      {},
    );

    for await (const page of paginator) {
      if (page.Topics.find((t) => t.TopicArn === topicArn)) {
        foundTopic = true;
        break;
      }
    }

    expect(foundTopic).toBe(false);
  });
});
