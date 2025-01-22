package com.example.sqs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagExamplesTest {
    String queueUrl = "";

    @BeforeEach
    void setUp() {
        String queueName = "TagExamples-queue-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        queueUrl = TagExamples.sqsClient.createQueue(b -> b
                .queueName(queueName)).queueUrl();
    }

    @AfterEach
    void tearDown() {
        TagExamples.sqsClient.deleteQueue(b -> b.queueUrl(queueUrl));
    }

    @Test
    @Tag("IntegrationTest")
    void addTags() {
        TagExamples.addTags(queueUrl);
        final Map<String, String> tags = TagExamples.sqsClient.listQueueTags(b -> b
                        .queueUrl(queueUrl)).tags();

        assertTrue(tags.containsKey("Team"));
        assertTrue(tags.containsKey("Priority"));
        assertTrue(tags.containsKey("Accounting ID"));
    }

    @Test
    @Tag("IntegrationTest")
    void listTags() {
        assertDoesNotThrow(() -> TagExamples.listTags(queueUrl));
    }

    @Test
    @Tag("IntegrationTest")
    void removeTags() {
        TagExamples.sqsClient.tagQueue(b -> b
                .queueUrl(queueUrl)
                .tags(Map.of("Accounting ID", "TestValue")));
        TagExamples.removeTags(queueUrl);
        final Map<String, String> tags = TagExamples.sqsClient.listQueueTags(b -> b
                .queueUrl(queueUrl)).tags();
        assertFalse(tags.containsKey("Accounting ID"));
    }
}