package com.example.firehoseingestion.ingestion;

import com.amazonaws.services.firehose.AmazonFirehose;
import com.amazonaws.services.firehose.AmazonFirehoseClientBuilder;
import com.amazonaws.services.firehose.model.PutRecordBatchRequest;
import com.amazonaws.services.firehose.model.PutRecordBatchResult;
import com.amazonaws.services.firehose.model.PutRecordRequest;
import com.amazonaws.services.firehose.model.PutRecordResult;
import com.amazonaws.services.firehose.model.Record;
import com.example.firehoseingestion.retry.ErrorHandling;

import java.nio.ByteBuffer;
import java.util.List;

public class FirehoseIngestion {

    private final AmazonFirehose firehoseClient;
    private final String deliveryStreamName;
    private final ErrorHandling errorHandling;

    public FirehoseIngestion(String deliveryStreamName, String region) {
        this.deliveryStreamName = deliveryStreamName;
        this.firehoseClient = AmazonFirehoseClientBuilder.standard().withRegion(region).build();
        this.errorHandling = new ErrorHandling();
    }

    public void putRecord(String data) {
        PutRecordRequest request = new PutRecordRequest()
                .withDeliveryStreamName(deliveryStreamName)
                .withRecord(new Record().withData(ByteBuffer.wrap(data.getBytes())));

        errorHandling.retryWithExponentialBackoff(() -> firehoseClient.putRecord(request));
    }

    public void putRecordBatch(List<Record> records) {
        PutRecordBatchRequest request = new PutRecordBatchRequest()
                .withDeliveryStreamName(deliveryStreamName)
                .withRecords(records);

        PutRecordBatchResult result = errorHandling.retryWithExponentialBackoff(() -> firehoseClient.putRecordBatch(request));

        if (result.getFailedPutCount() > 0) {
            // Handle failed records
        }
    }
}