/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.cloudwatch.DeleteAlarm;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import com.example.cloudwatch.*;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudWatchTest {

    private static CloudWatchClient cw ;
    private static CloudWatchLogsClient cloudWatchLogsClient ;
    private static CloudWatchEventsClient cwe;
    private static String logGroup="";
    private static String alarmName="";
    private static String streamName ="";
    private static String metricId = "";
    private static String instanceId="";
    private static String ruleResource = "";
    private static String filterName="";
    private static String destinationArn="";
    private static String roleArn ="";
    private static String ruleArn ="";
    private static String namespace ="";
    private static String filterPattern = "";
    private static String ruleName = "";


    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        cw = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        cwe = CloudWatchEventsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = CloudWatchTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            logGroup = prop.getProperty("logGroup");
            alarmName = prop.getProperty("alarmName");
            streamName = prop.getProperty("streamName");
            ruleResource = prop.getProperty("ruleResource");
            metricId = prop.getProperty("metricId");
            filterName = prop.getProperty("filterName");
            destinationArn = prop.getProperty("destinationArn");
            roleArn= prop.getProperty("roleArn");
            filterPattern= prop.getProperty("filterPattern");
            instanceId= prop.getProperty("instanceId");
            ruleName= prop.getProperty("ruleName");
            ruleArn= prop.getProperty("ruleArn");
            namespace= prop.getProperty("namespace");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSCWService_thenNotNull() {
        assertNotNull(cw);
        assertNotNull(cloudWatchLogsClient);
        System.out.printf("\n Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateAlarm() {
        PutMetricAlarm.putMetricAlarm(cw, alarmName,instanceId );
        System.out.printf("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeAlarms() {

       DescribeAlarms.desCWAlarms(cw);
       System.out.printf("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreateSubscriptionFilters() {
      PutSubscriptionFilter.putSubFilters(cloudWatchLogsClient, filterName, filterPattern, logGroup, destinationArn);
       System.out.printf("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeSubscriptionFilters() {
       DescribeSubscriptionFilters.describeFilters(cloudWatchLogsClient,logGroup);
       System.out.printf("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void DisableAlarmActions() {

      DisableAlarmActions.disableActions(cw, alarmName);
      System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void EnableAlarmActions() {

      EnableAlarmActions.enableActions(cw, alarmName) ;
      System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void GetLogEvents() {

        GetLogEvents.getCWLogEvents(cloudWatchLogsClient,logGroup,streamName);
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(9)
    void PutCloudWatchEvent() {
       PutEvents.putCWEvents(cwe,ruleResource );
       System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void GetMetricData() {

      GetMetricData.getMetData(cw);
      System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void DeleteSubscriptionFilter() {

        DeleteSubscriptionFilter.deleteSubFilter(cloudWatchLogsClient, filterName,logGroup );
        System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
   public void PutRule() {
       PutRule.putCWRule(cwe, ruleName, ruleArn);
        System.out.println("\n Test 12 passed");
   }

    @Test
    @Order(13)
   public void ListMetrics() {
       ListMetrics.listMets(cw, namespace);
       System.out.println("\n Test 13 passed");
   }

    @Test
    @Order(14)
   public void PutLogEvents() {
       PutLogEvents.putCWLogEvents(cloudWatchLogsClient, logGroup, streamName);
        System.out.println("\n Test 14 passed");
   }

    @Test
    @Order(15)
    public void DeleteAlarm() {

      DeleteAlarm.deleteCWAlarm(cw, alarmName);
      System.out.println("\n Test 15 passed");
    }
}





