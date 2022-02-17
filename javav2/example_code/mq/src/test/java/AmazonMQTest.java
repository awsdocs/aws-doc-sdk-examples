/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.mq.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.Configuration;
import software.amazon.awssdk.services.mq.model.BrokerSummary;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonMQTest {

    private static MqClient mqClient ;
    private static Region region;
    private static String engineType = "";
    private static String brokerName = "";
    private static String configurationName = "";
    private static String brokerId = "";
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

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            engineType = prop.getProperty("engineType");
            brokerName = prop.getProperty("brokerName");
            configurationName = prop.getProperty("configurationName");
            brokerId = prop.getProperty("brokerId");
            configurationId = prop.getProperty("configurationId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingMQ_thenNotNull() {
        assertNotNull(mqClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateBroker() {
        String result = CreateBroker.createBroker(mqClient, engineType, brokerName);
        assertTrue(!result.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateConfiguration() {
        String result = CreateConfiguration.createNewConfigutation(mqClient, configurationName);
        assertTrue(!result.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeBroker() {
        String result = DescribeBroker.describeBroker(mqClient, brokerName);
        assertTrue(!result.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListBrokers() {
        List<BrokerSummary> result = ListBrokers.listBrokers(mqClient);
        assertTrue(result instanceof List<?>);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListConfigurations() {
        List<Configuration> result = ListConfigurations.listConfigurations(mqClient);
        assertTrue(result instanceof List<?>);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void UpdateBrokerConfiguration() {
        String result = UpdateBrokerConfiguration.updateBrokerConfiguration(mqClient, brokerId, configurationId);
        assertTrue(!result.isEmpty());
        System.out.print("Test 7 passed");
    }
}
