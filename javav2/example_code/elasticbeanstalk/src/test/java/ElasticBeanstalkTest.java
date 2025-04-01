// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.aws.example.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ElasticBeanstalkTest {
    private static final Logger logger = LoggerFactory.getLogger(ElasticBeanstalkTest.class);
    private static ElasticBeanstalkClient beanstalkClient;
    private static final String appName = "apptest";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        beanstalkClient = ElasticBeanstalkClient.builder()
                .region(region)
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateApp() {
        String appArn = CreateApplication.createApp(beanstalkClient, appName);
        assertFalse(appArn.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateEnvironment() {
        String envName = "environmenttest";
        String environmentArn = CreateEnvironment.createEBEnvironment(beanstalkClient, envName, appName);
        assertFalse(environmentArn.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeApplications() {
        DescribeApplications.describeApps(beanstalkClient);
        assertTrue(true);
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDescribeEnvironment() {
        assertDoesNotThrow(() -> DescribeEnvironment.describeEnv(beanstalkClient, appName));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDeleteApplication() throws InterruptedException {
        System.out.println("*** Wait for 5 MIN so the app can be deleted");
        TimeUnit.MINUTES.sleep(5);
        assertDoesNotThrow(() -> DeleteApplication.deleteApp(beanstalkClient, appName));
        logger.info("Test 5 passed");
    }
}
