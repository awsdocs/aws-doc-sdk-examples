// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.kinesis.CreateDataStream;
import com.example.kinesis.DescribeLimits;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import com.example.kinesis.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KinTest {
    private static final Logger logger = LoggerFactory.getLogger(KinTest.class);
    private static KinesisClient kinesisClient;
    private static String streamName = "";

    @BeforeAll
    public static void setUp() {
        kinesisClient = KinesisClient.builder()
                .region(Region.US_EAST_1)
                .build();
        streamName = "streamName" + java.util.UUID.randomUUID();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateDataStream() {
        assertDoesNotThrow(() -> CreateDataStream.createStream(kinesisClient, streamName));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeLimits() {
        assertDoesNotThrow(() -> DescribeLimits.describeKinLimits(kinesisClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testListShards() {
        try {
            // Wait 60 secs for table to complete
            TimeUnit.SECONDS.sleep(60);
            assertDoesNotThrow(() -> ListShards.listKinShards(kinesisClient, streamName));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testPutRecords() {
        assertDoesNotThrow(() -> StockTradesWriter.setStockData(kinesisClient, streamName));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testGetRecords() {
        assertDoesNotThrow(() -> GetRecords.getStockTrades(kinesisClient, streamName));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDeleteDataStreem() {
        assertDoesNotThrow(() -> DeleteDataStream.deleteStream(kinesisClient, streamName));
        logger.info("Test 6 passed");
    }
}
