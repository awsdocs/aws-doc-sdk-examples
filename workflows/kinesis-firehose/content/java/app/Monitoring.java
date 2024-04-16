package com.example.firehoseingestion.monitoring;

import com.amazonaws.services.firehose.AmazonFirehose;
import com.amazonaws.services.firehose.AmazonFirehoseClientBuilder;
import com.amazonaws.services.firehose.model.DescribeDeliveryStreamRequest;
import com.amazonaws.services.firehose.model.DescribeDeliveryStreamResult;
import com.amazonaws.services.firehose.model.DeliveryStreamDescription;

public class Monitoring {

    private final AmazonFirehose firehoseClient;
    private final String deliveryStreamName;

    public Monitoring(String deliveryStreamName, String region) {
        this.deliveryStreamName = deliveryStreamName;
        this.firehoseClient = AmazonFirehoseClientBuilder.standard().withRegion(region).build();
    }

    public void logIngestingMetrics() {
        DescribeDeliveryStreamRequest request = new DescribeDeliveryStreamRequest()
                .withDeliveryStreamName(deliveryStreamName);

        DescribeDeliveryStreamResult result = firehoseClient.describeDeliveryStream(request);
        DeliveryStreamDescription description = result.getDeliveryStreamDescription();

        System.out.println("IncomingBytes: " + description.getIncomingBytes());
        System.out.println("IncomingRecords: " + description.getIncomingRecords());
    }
}