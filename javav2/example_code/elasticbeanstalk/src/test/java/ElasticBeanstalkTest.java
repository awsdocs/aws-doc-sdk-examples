/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.aws.example.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ElasticBeanstalkTest {
    private static ElasticBeanstalkClient beanstalkClient;
    private static final String appName="apptest";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        beanstalkClient = ElasticBeanstalkClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateApp() {
        String appArn = CreateApplication.createApp(beanstalkClient, appName);
        assertFalse(appArn.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateEnvironment() {
        String envName = "environmenttest";
        String environmentArn = CreateEnvironment.createEBEnvironment(beanstalkClient, envName, appName);
        assertFalse(environmentArn.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeApplications() {
        DescribeApplications.describeApps(beanstalkClient);
        assertTrue(true);
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DescribeEnvironment() {
        assertDoesNotThrow(() ->DescribeEnvironment.describeEnv(beanstalkClient, appName));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DeleteApplication() throws InterruptedException {
      System.out.println("*** Wait for 5 MIN so the app can be deleted");
      TimeUnit.MINUTES.sleep(5);
        assertDoesNotThrow(() ->DeleteApplication.deleteApp(beanstalkClient, appName));
      System.out.println("Test 5 passed");
  }
}
