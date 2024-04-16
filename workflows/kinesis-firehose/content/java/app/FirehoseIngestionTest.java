package com.example.firehoseingestion.ingestion;

import com.amazonaws.services.firehose.model.PutRecordBatchResult;
import com.amazonaws.services.firehose.model.PutRecordResult;
import com.amazonaws.services.firehose.model.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FirehoseIngestionTest {

    private FirehoseIngestion firehoseIngestion;
    private AmazonFirehose firehoseClient;

    @BeforeEach
    void setUp() {
        firehoseClient = mock(AmazonFirehose.class);
        firehoseIngestion = new FirehoseIngestion("test-stream", "us-west-2", firehoseClient);
    }

    @Test
    void testPutRecord() {
        String data = "Sample data record";
        PutRecordResult expectedResult = new PutRecordResult();
        when(firehoseClient.putRecord(any())).thenReturn(expectedResult);

        PutRecordResult result = firehoseIngestion.putRecord(data);

        assertEquals(expectedResult, result);
        ArgumentCaptor<PutRecordRequest> captor = ArgumentCaptor.forClass(PutRecordRequest.class);
        verify(firehoseClient, times(1)).putRecord(captor.capture());

        PutRecordRequest request = captor.getValue();
        assertEquals("test-stream", request.getDeliveryStreamName());
        assertEquals(ByteBuffer.wrap(data.getBytes()), request.getRecord().getData());
    }

    @Test
    void testPutRecordBatch() {
        List<Record> records = Arrays.asList(
                new Record().withData(ByteBuffer.wrap("Record 1".getBytes())),
                new Record().withData(ByteBuffer.wrap("Record 2".getBytes()))
        );
        PutRecordBatchResult expectedResult = new PutRecordBatchResult().withFailedPutCount(0);
        when(firehoseClient.putRecordBatch(any())).thenReturn(expectedResult);

        PutRecordBatchResult result = firehoseIngestion.putRecordBatch(records);

        assertEquals(expectedResult, result);
        ArgumentCaptor<PutRecordBatchRequest> captor = ArgumentCaptor.forClass(PutRecordBatchRequest.class);
        verify(firehoseClient, times(1)).putRecordBatch(captor.capture());

        PutRecordBatchRequest request = captor.getValue();
        assertEquals("test-stream", request.getDeliveryStreamName());
        assertEquals(records, request.getRecords());
    }
}