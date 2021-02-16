/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.mq.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.CreateConfigurationRequest;

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
            brokerName=prop.getProperty("brokerName");
            configurationName= prop.getProperty("configurationName");We

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

        String brokerId = CreateBroker.createBroker(mqClient, engineType, brokerName);
        assertTrue(!brokerId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateConfiguration() {

        String configurationId = CreateConfiguration.createNewConfigutation(mqClient, configurationName);
        assertTrue(!configurationId.isEmpty());
        System.out.println("Test 2 passed");
    }
}
