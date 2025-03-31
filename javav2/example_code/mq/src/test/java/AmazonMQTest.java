// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.mq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.Configuration;
import software.amazon.awssdk.services.mq.model.BrokerSummary;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonMQTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonMQTest.class);
    private static MqClient mqClient;
    private static Region region;
    private static String engineType = "";
    private static String brokerName = "";
    private static String brokerId = "";
    private static String configurationName = "";
    private static String configurationId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        mqClient = MqClient.builder()
                .region(region)
                .build();

        try (InputStream input = AmazonMQTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Unable to find config.properties.");
                return;
            }

            // load a properties file from class path, inside static method
            prop.load(input);
            Random random = new Random();
            int randomValue = random.nextInt(1000) + 1;
            // Populate the data members required for all tests
            engineType = prop.getProperty("engineType");
            brokerName = prop.getProperty("brokerName")+randomValue;
            configurationName = prop.getProperty("configurationName");
            configurationId = prop.getProperty("configurationId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateBroker() {
        brokerId = CreateBroker.createBroker(mqClient, engineType, brokerName);
        assertTrue(!brokerId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateConfiguration() {
        String result = CreateConfiguration.createNewConfigutation(mqClient, configurationName);
        assertTrue(!result.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeBroker() {
        String result = DescribeBroker.describeBroker(mqClient, brokerName);
        assertTrue(!result.isEmpty());
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListBrokers() {
        List<BrokerSummary> result = ListBrokers.listBrokers(mqClient);
        assertTrue(result instanceof List<?>);
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListConfigurations() {
        List<Configuration> result = ListConfigurations.listConfigurations(mqClient);
        assertTrue(result instanceof List<?>);
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDeleteBroker() {
        assertDoesNotThrow(() -> DeleteBroker.deleteBroker(mqClient, brokerId));
        logger.info("Test 6 passed");
    }
}
