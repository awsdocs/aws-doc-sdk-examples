package com.example.firehoseingestion.monitoring;

import com.amazonaws.services.firehose.AmazonFirehose;
import com.amazonaws.services.firehose.model.DescribeDeliveryStreamRequest;
import com.amazonaws.services.firehose.model.DescribeDeliveryStreamResult;
import com.amazonaws.services.firehose.model.DeliveryStreamDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MonitoringTest {

    private Monitoring monitoring;
    private AmazonFirehose firehoseClient;

    @BeforeEach
    void setUp() {
        firehoseClient = mock(AmazonFirehose.class);
        monitoring = new Monitoring("test-stream", "us-west-2", firehoseClient);
    }

    @Test
    void testLogIngestingMetrics() {
        DeliveryStreamDescription description = new DeliveryStreamDescription()
                .withIncomingBytes(1234L)
                .withIncomingRecords(10L);
        DescribeDeliveryStreamResult result = new DescribeDeliveryStreamResult().withDeliveryStreamDescription(description);
        when(firehoseClient.describeDeliveryStream(any())).thenReturn(result);

        monitoring.logIngestingMetrics();

        ArgumentCaptor<DescribeDeliveryStreamRequest> captor = ArgumentCaptor.forClass(DescribeDeliveryStreamRequest.class);
        verify(firehoseClient, times(1)).describeDeliveryStream(captor.capture());

        DescribeDeliveryStreamRequest request = captor.getValue();
        assertEquals("test-stream", request.getDeliveryStreamName());
    }
}