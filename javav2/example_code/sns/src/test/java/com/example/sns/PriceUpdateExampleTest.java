package com.example.sns;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.sns.PriceUpdateExample.addAccessPolicyToQueuesFINAL;
import static com.example.sns.PriceUpdateExample.createFIFOTopic;
import static com.example.sns.PriceUpdateExample.createQueues;
import static com.example.sns.PriceUpdateExample.deleteQueues;
import static com.example.sns.PriceUpdateExample.deleteSubscriptions;
import static com.example.sns.PriceUpdateExample.deleteTopic;
import static com.example.sns.PriceUpdateExample.publishPriceUpdate;
import static com.example.sns.PriceUpdateExample.sqsClient;
import static com.example.sns.PriceUpdateExample.subscribeQueues;

class PriceUpdateExampleTest {

    @Test
    @Tag("IntegrationTest")
    void publishPriceUpdateTest() {
        String topicName = "MyTestTopic.fifo";
        String wholesaleQueueName = "wholesaleQueue.fifo";
        String retailQueueName = "retailQueue.fifo";
        String analyticsQueueName = "analyticsQueue";

        List<PriceUpdateExample.QueueData> queues = List.of(
                new PriceUpdateExample.QueueData(wholesaleQueueName, PriceUpdateExample.QueueType.FIFO),
                new PriceUpdateExample.QueueData(retailQueueName, PriceUpdateExample.QueueType.FIFO),
                new PriceUpdateExample.QueueData(analyticsQueueName, PriceUpdateExample.QueueType.Standard));

        createQueues(queues);
        String topicARN = createFIFOTopic(topicName);
        subscribeQueues(queues, topicARN);
        addAccessPolicyToQueuesFINAL(queues, topicARN);
        publishPriceUpdate(topicARN, "{\"product\": 214, \"price\": 79.99}", "Consumables");

        // Assert that each queue received the message published
        queues.forEach(queue -> {
            queue.testMessage = sqsClient.receiveMessage(builder -> builder.queueUrl(queue.queueURL).maxNumberOfMessages(1)).messages().get(0);
            Assertions.assertNotNull(queue.testMessage);
        });

        deleteSubscriptions(queues);
        deleteQueues(queues);
        deleteTopic(topicARN);
    }
}


