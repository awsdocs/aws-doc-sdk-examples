import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.*;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.io.*;
import java.time.Instant;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudWatchServiceIntegrationTest {

    private static CloudWatchClient cw = null;
    private static CloudWatchLogsClient cloudWatchLogsClient = null;
    private static String logGroup="";
    private static String alarmName="";
    private static String streamName ="";
    private static String metricId = "";
    private static String instanceId="";
    private static String ruleResource = "";
    private static String filterName="";
    private static String destinationArn="";
    private static String roleArn ="";
    private static String filterPattern = "";

    @BeforeAll
    public static void setUp() throws IOException {

        cw = CloudWatchClient.builder().build();
        cloudWatchLogsClient = CloudWatchLogsClient.builder().build();

        try (InputStream input = CloudWatchServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSCWService_thenNotNull() {
        assertNotNull(cw);
        assertNotNull(cloudWatchLogsClient);
        System.out.printf("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateAlarm() {

        try {

            Dimension dimension = Dimension.builder()
                    .name("InstanceId")
                    .value(instanceId).build();

            PutMetricAlarmRequest request = PutMetricAlarmRequest.builder()
                    .alarmName(alarmName)
                    .comparisonOperator(
                            ComparisonOperator.GREATER_THAN_THRESHOLD)
                    .evaluationPeriods(1)
                    .metricName("CPUUtilization")
                    .namespace("AWS/EC2")
                    .period(60)
                    .statistic(Statistic.AVERAGE)
                    .threshold(70.0)
                    .actionsEnabled(false)
                    .alarmDescription(
                            "Alarm when server CPU utilization exceeds 70%")
                    .unit(StandardUnit.SECONDS)
                    .dimensions(dimension)
                    .build();

            PutMetricAlarmResponse response = cw.putMetricAlarm(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeAlarms() {

        try {

            boolean done = false;
            String newToken = null;

            while(!done) {
                DescribeAlarmsResponse response;

                if (newToken == null) {
                    DescribeAlarmsRequest request = DescribeAlarmsRequest.builder().build();
                    response = cw.describeAlarms(request);
                } else {
                    DescribeAlarmsRequest request = DescribeAlarmsRequest.builder()
                            .nextToken(newToken)
                            .build();
                    response = cw.describeAlarms(request);
                }

                for(MetricAlarm alarm : response.metricAlarms()) {
                    System.out.printf("\n Retrieved alarm %s", alarm.alarmName());
                }

                if(response.nextToken() == null) {
                    done = true;
                } else {
                    newToken = response.nextToken();
                }
            }

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.printf("Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreateSubscriptionFilters() {

       try {

        PutSubscriptionFilterRequest filRequest = PutSubscriptionFilterRequest.builder()
                .filterName(filterName)
                .logGroupName(logGroup)
                .destinationArn(destinationArn)
                .roleArn(roleArn)
                .filterPattern(filterPattern)
                .build();

           cloudWatchLogsClient.putSubscriptionFilter(filRequest);
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeSubscriptionFilters() {

        try {

            boolean done = false;
            String newToken = null;

            while(!done) {

                DescribeSubscriptionFiltersResponse response;

                if (newToken == null) {
                    DescribeSubscriptionFiltersRequest request =
                            DescribeSubscriptionFiltersRequest.builder()
                                    .logGroupName(logGroup)
                                    .limit(1).build();

                    response = cloudWatchLogsClient.describeSubscriptionFilters(request);
                } else {
                    DescribeSubscriptionFiltersRequest request =
                            DescribeSubscriptionFiltersRequest.builder()
                                    .nextToken(newToken)
                                    .logGroupName(logGroup)
                                    .limit(1).build();

                    response = cloudWatchLogsClient.describeSubscriptionFilters(request);
                }

                for(SubscriptionFilter filter : response.subscriptionFilters()) {
                    System.out.printf(
                            "Retrieved filter with name %s, " +
                                    "pattern %s " +
                                    "log group %s " +
                                    "and destination arn %s",
                            filter.filterName(),
                            filter.filterPattern(),
                            filter.logGroupName(),
                            filter.destinationArn());
                    System.out.println("");
                }

                if(response.nextToken() == null) {
                    done = true;
                } else {
                    newToken = response.nextToken();
                }
            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.printf("Test 5 passed");
    }


    @Test
    @Order(6)
    public void DisableAlarmActions() {

       try {

           DisableAlarmActionsRequest request = DisableAlarmActionsRequest.builder()
                    .alarmNames(alarmName).build();

            DisableAlarmActionsResponse response = cw.disableAlarmActions(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void EnableAlarmActions() {

       try {
            EnableAlarmActionsRequest request = EnableAlarmActionsRequest.builder()
                    .alarmNames(alarmName).build();

            EnableAlarmActionsResponse response = cw.enableAlarmActions(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void GetLogEvents() {

        try {
             GetLogEventsRequest getLogEventsRequest = GetLogEventsRequest.builder()
                    .logGroupName(logGroup)
                    .logStreamName(streamName)
                    .startFromHead(true)
                    .build();

            int logLimit = cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().size();
            for (int c = 0; c < logLimit; c++) {
                // Prints the messages to the console
                System.out.println(cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().get(c).message());
            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 8 passed");
    }


    @Test
    @Order(9)
    void PutCloudWatchEvent() {

        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder().build();

        try {

            final String EVENT_DETAILS =
                    "{ \"key1\": \"value1\", \"key2\": \"value2\" }";

            PutEventsRequestEntry requestEntry = PutEventsRequestEntry.builder()
                    .detail(EVENT_DETAILS)
                    .detailType("sampleSubmitted")
                    .resources(ruleResource)
                    .source("aws-sdk-java-cloudwatch-example").build();

            PutEventsRequest request = PutEventsRequest.builder()
                    .entries(requestEntry).build();

            cwe.putEvents(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void GetMetricData() {

       try {

            // Set the date
            Instant start = Instant.ofEpochMilli(new Date().getTime());
            start = Instant.parse("2019-10-23T10:12:35Z");

            Instant endDate = Instant.now();

            Metric met = Metric.builder()
                    .metricName("DiskReadBytes")
                    .namespace("AWS/EC2")
                    .build();

            MetricStat metStat = MetricStat.builder()
                    .stat("Minimum")
                    .period(60)
                    .metric(met)
                    .build();

            MetricDataQuery dataQUery = MetricDataQuery.builder()
                    .metricStat(metStat)
                    .id(metricId)
                    .returnData(true)
                    .build();

            List<MetricDataQuery> dq = new ArrayList();
            dq.add(dataQUery);

            GetMetricDataRequest getMetReq = GetMetricDataRequest.builder()
                    .maxDatapoints(100)
                    .startTime(start)
                    .endTime(endDate)
                    .metricDataQueries(dq)
                    .build();

            GetMetricDataResponse response = cw.getMetricData(getMetReq);

            List<MetricDataResult> data = response.metricDataResults();

            for (int i = 0; i < data.size(); i++) {

                MetricDataResult item = (MetricDataResult) data.get(i);
                System.out.println("The label is "+item.label());
                System.out.println("The status code is "+item.statusCode().toString());
            }

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void DeleteSubscriptionFilter() {

        try {

          DeleteSubscriptionFilterRequest request =
                    DeleteSubscriptionFilterRequest.builder()
                            .filterName(filterName)
                           .logGroupName(logGroup).build();

           cloudWatchLogsClient.deleteSubscriptionFilter(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void DeleteAlarm() {

      try {
        DeleteAlarmsRequest delAlarm = DeleteAlarmsRequest.builder()
                .alarmNames(alarmName)
                .build();

        cw.deleteAlarms(delAlarm);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 12 passed");
    }
}
