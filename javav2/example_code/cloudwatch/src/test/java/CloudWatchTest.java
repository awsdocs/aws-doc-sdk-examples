/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.cloudwatch.DeleteAlarm;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import com.example.cloudwatch.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
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
    private static String myDateSc = "";
    private static String costDateWeekSc = "";
    private static String dashboardNameSc = "";
    private static String dashboardJsonSc = "";
    private static String dashboardAddSc = "";
    private static String settingsSc = "";
    private static String metricImageSc = "";

    @BeforeAll
    public static void setUp() throws IOException {
        cw = CloudWatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        cloudWatchLogsClient = CloudWatchLogsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        cwe = CloudWatchEventsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        logGroup = values.getLogGroup();
        alarmName = values.getAlarmName();
        streamName = values.getStreamName();
        ruleResource = values.getRuleResource();
        metricId = values.getMetricId();
        filterName = values.getFilterName();
        destinationArn = values.getDestinationArn();
        roleArn = values.getRoleArn();
        filterPattern = values.getFilterPattern();
        instanceId = values.getInstanceId();
        ruleName =  values.getRuleName();
        ruleArn = values.getRuleArn();
        namespace = values.getNamespace();
        myDateSc= values.myDateSc;
        costDateWeekSc= values.getCostDateWeekSc();
        dashboardNameSc = values.getDashboardNameSc();
        dashboardJsonSc= values.getDashboardJsonSc();
        dashboardAddSc= values.getDashboardAddSc();
        settingsSc= values.getSettingsSc();
        metricImageSc= values.getMetricImageSc();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = CloudWatchTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests
            prop.load(input);
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
            myDateSc= prop.getProperty("myDateSc");
            costDateWeekSc= prop.getProperty("costDateWeekSc");
            dashboardNameSc= prop.getProperty("dashboardNameSc");
            dashboardJsonSc= prop.getProperty("dashboardJsonSc");
            dashboardAddSc= prop.getProperty("dashboardAddSc");
            settingsSc= prop.getProperty("settingsSc");
            metricImageSc= prop.getProperty("metricImageSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateAlarm() {
        assertDoesNotThrow(() ->PutMetricAlarm.putMetricAlarm(cw, alarmName,instanceId));
        System.out.println(" Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeAlarms() {
       assertDoesNotThrow(() ->DescribeAlarms.desCWAlarms(cw));
       System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeSubscriptionFilters() {
       assertDoesNotThrow(() ->DescribeSubscriptionFilters.describeFilters(cloudWatchLogsClient,logGroup));
       System.out.println(" Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DisableAlarmActions() {
      assertDoesNotThrow(() ->DisableAlarmActions.disableActions(cw, alarmName));
      System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void EnableAlarmActions() {
      assertDoesNotThrow(() ->EnableAlarmActions.enableActions(cw, alarmName));
      System.out.println("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    void PutCloudWatchEvent() {
       assertDoesNotThrow(() -> PutEvents.putCWEvents(cwe,ruleResource));
       System.out.println("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void GetMetricData() {
      assertDoesNotThrow(() ->GetMetricData.getMetData(cw));
      System.out.println("\n Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
   public void PutRule() {
        assertDoesNotThrow(() ->PutRule.putCWRule(cwe, ruleName, ruleArn));
        System.out.println("\n Test 8 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
   public void ListMetrics() {
       assertDoesNotThrow(() ->ListMetrics.listMets(cw, namespace));
       System.out.println("\n Test 9 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void DeleteAlarm() {
        assertDoesNotThrow(() ->DeleteAlarm.deleteCWAlarm(cw, alarmName));
        System.out.println("\n Test 10 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void CloudWatchScenarioTest() {
        Double dataPoint = Double.parseDouble("10.0");
        System.out.println("1. List at least five available unique namespaces from Amazon CloudWatch. Select one from the list.");
        ArrayList<String> list = CloudWatchScenario.listNameSpaces(cw);
        for (int z=0; z<5; z++) {
            int index = z+1;
            System.out.println("    " +index +". " +list.get(z));
        }

        String selectedNamespace = "";
        String selectedMetrics = "";
        int num = 2;
        if (1 <= num && num <= 5){
            selectedNamespace = list.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+selectedNamespace);

        System.out.println("2. List available metrics within the selected namespace and select one from the list.");
        ArrayList<String> metList = CloudWatchScenario.listMets(cw, selectedNamespace);
        for (int z=0; z<5; z++) {
            int index = z+1;
            System.out.println("    " +index +". " +metList.get(z));
        }
        num = 1;
        if (1 <= num && num <= 5){
            selectedMetrics = metList.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+selectedMetrics);
        Dimension myDimension = CloudWatchScenario.getSpecificMet( cw, selectedNamespace);

        System.out.println("3. Get statistics for the selected metric over the last day.");
        String metricOption="";
        ArrayList<String> statTypes = new ArrayList<>();
        statTypes.add("SampleCount");
        statTypes.add("Average");
        statTypes.add("Sum");
        statTypes.add("Minimum");
        statTypes.add("Maximum");

        for (int t=0; t<5; t++){
            System.out.println("    " +(t+1) +". "+statTypes.get(t));
        }
        System.out.println("Select a metric statistic by entering a number from the preceding list:");
        num = Integer.parseInt("2");
        if (1 <= num && num <= 5){
            metricOption = statTypes.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+metricOption);
        CloudWatchScenario.getAndDisplayMetricStatistics(cw, selectedNamespace, selectedMetrics, metricOption, myDateSc, myDimension);
        System.out.println("4. Get CloudWatch estimated billing for the last week.");
        CloudWatchScenario.getMetricStatistics(cw, costDateWeekSc);

        System.out.println("5. Create a new CloudWatch dashboard with metrics.");
        CloudWatchScenario.createDashboardWithMetrics(cw, dashboardNameSc, dashboardJsonSc);
        System.out.println("6. List dashboards using a paginator.");
        CloudWatchScenario.listDashboards(cw);

        System.out.println("7. Create a new custom metric by adding data to it.");
        CloudWatchScenario.createNewCustomMetric(cw, dataPoint);

        System.out.println("8. Add an additional metric to the dashboard.");
        CloudWatchScenario.addMetricToDashboard(cw, dashboardAddSc, dashboardNameSc);
        System.out.println("9. Create an alarm for the custom metric.");
        String alarmName = CloudWatchScenario.createAlarm(cw, settingsSc);
        System.out.println("10. Describe ten current alarms.");
        CloudWatchScenario.describeAlarms(cw);
        System.out.println("11. Get current data for new custom metric.");
        CloudWatchScenario.getCustomMetricData(cw,settingsSc);
        System.out.println("12. Push data into the custom metric to trigger the alarm.");
        CloudWatchScenario.addMetricDataForAlarm(cw, settingsSc) ;
        System.out.println("13. Check the alarm state using the action DescribeAlarmsForMetric.");
        CloudWatchScenario.checkForMetricAlarm(cw, settingsSc);
        System.out.println("14. Get alarm history for the new alarm.");
        CloudWatchScenario.getAlarmHistory(cw, settingsSc, myDateSc);
        System.out.println("15. Add an anomaly detector for the custom metric.");
        CloudWatchScenario.addAnomalyDetector(cw, settingsSc);
        System.out.println("16. Describe current anomaly detectors.");
        CloudWatchScenario.describeAnomalyDetectors(cw, settingsSc);
        System.out.println("17. Get a metric image for the custom metric.");
        CloudWatchScenario.getAndOpenMetricImage(cw, metricImageSc);
        System.out.println("18. Clean up the Amazon CloudWatch resources.");
        CloudWatchScenario.deleteDashboard(cw, dashboardNameSc);
        CloudWatchScenario.deleteCWAlarm(cw, alarmName);
        CloudWatchScenario.deleteAnomalyDetector(cw, settingsSc);
        System.out.println("The Amazon CloudWatch example scenario is complete.");
    }


    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/cloudwatch";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudwatch (an AWS Secrets Manager secret)")
    class SecretValues {
        private String logGroup;
        private String alarmName;
        private String instanceId;

        private String streamName;

        private String ruleResource;

        private String metricId;

        private String filterName;

        private String destinationArn;

        private String roleArn;

        private String ruleArn;

        private String filterPattern;

        private String ruleName;

        private String namespace;

        private String myDateSc;

        private String costDateWeekSc;

        private String dashboardNameSc;

        private String dashboardJsonSc;

        private String dashboardAddSc;

        private String settingsSc;

        private String metricImageSc;

        // Provide getter methods for each of the test values
        public String getLogGroup() {
            return logGroup;
        }

        public String getAlarmName() {
            return alarmName;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getStreamName() {
            return streamName;
        }

        public String getRuleResource() {
            return ruleResource;
        }

        public String getMetricId() {
            return metricId;
        }

        public String getFilterName() {
            return filterName;
        }

        public String getDestinationArn() {
            return destinationArn;
        }

        public String getRoleArn() {
            return roleArn;
        }

        public String getFilterPattern() {
            return filterPattern;
        }

        public String getRuleName() {
            return ruleName;
        }

        public String getRuleArn() {
            return ruleArn;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getMyDateSc() {
            return myDateSc;
        }

        public String getCostDateWeekSc() {
            return costDateWeekSc;
        }

        public String getDashboardNameSc() {
            return dashboardNameSc;
        }

        public String getDashboardJsonSc() {
            return dashboardJsonSc;
        }

        public String getDashboardAddSc() {
            return dashboardAddSc;
        }

        public String getSettingsSc() {
            return settingsSc;
        }

        public String getMetricImageSc() {
            return metricImageSc;
        }
    }
 }





