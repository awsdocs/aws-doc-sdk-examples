//snippet-sourcedescription:[CloudWatchScenario.java demonstrates how to perform various operations by using Amazon CloudWatch.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudwatch;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.AlarmHistoryItem;
import software.amazon.awssdk.services.cloudwatch.model.AlarmType;
import software.amazon.awssdk.services.cloudwatch.model.AnomalyDetector;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.ComparisonOperator;
import software.amazon.awssdk.services.cloudwatch.model.DashboardValidationMessage;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAlarmsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAnomalyDetectorRequest;
import software.amazon.awssdk.services.cloudwatch.model.DeleteDashboardsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmHistoryRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmHistoryResponse;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmsForMetricRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmsForMetricResponse;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmsResponse;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAnomalyDetectorsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAnomalyDetectorsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricWidgetImageRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricWidgetImageResponse;
import software.amazon.awssdk.services.cloudwatch.model.HistoryItemType;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricAlarm;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.PutAnomalyDetectorRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutDashboardRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutDashboardResponse;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.ScanBy;
import software.amazon.awssdk.services.cloudwatch.model.SingleMetricAnomalyDetector;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.cloudwatch.paginators.ListDashboardsIterable;
import software.amazon.awssdk.services.cloudwatch.paginators.ListMetricsIterable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// snippet-start:[cloudwatch.java2.scenario.main]
/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * To enable billing metrics and statistics for this example, make sure billing alerts are enabled for your account:
 * https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/monitor_estimated_charges_with_cloudwatch.html#turning_on_billing_metrics
 *
 * This Java code example performs the following tasks:
 *
 * 1. List available namespaces from Amazon CloudWatch.
 * 2. List available metrics within the selected Namespace.
 * 3. Get statistics for the selected metric over the last day.
 * 4. Get CloudWatch estimated billing for the last week.
 * 5. Create a new CloudWatch dashboard with metrics.
 * 6. List dashboards using a paginator.
 * 7. Create a new custom metric by adding data for it.
 * 8. Add the custom metric to the dashboard.
 * 9. Create an alarm for the custom metric.
 * 10. Describe current alarms.
 * 11. Get current data for the new custom metric.
 * 12. Push data into the custom metric to trigger the alarm.
 * 13. Check the alarm state using the action DescribeAlarmsForMetric.
 * 14. Get alarm history for the new alarm.
 * 15. Add an anomaly detector for the custom metric.
 * 16. Describe current anomaly detectors.
 * 17. Get a metric image for the custom metric.
 * 18. Clean up the Amazon CloudWatch resources.
 */
public class CloudWatchScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    public static void main(String[] args) throws IOException {
        final String usage = "\n" +
            "Usage:\n" +
            "  <myDate> <costDateWeek> <dashboardName> <dashboardJson> <dashboardAdd> <settings> <metricImage>  \n\n" +
            "Where:\n" +
            "  myDate - The start date to use to get metric statistics. (For example, 2023-01-11T18:35:24.00Z.) \n" +
            "  costDateWeek - The start date to use to get AWS/Billinget statistics. (For example, 2023-01-11T18:35:24.00Z.) \n" +
            "  dashboardName - The name of the dashboard to create. \n" +
            "  dashboardJson - The location of a JSON file to use to create a dashboard. (See Readme file.) \n" +
            "  dashboardAdd - The location of a JSON file to use to update a dashboard. (See Readme file.) \n" +
            "  settings - The location of a JSON file from which various values are read. (See Readme file.) \n" +
            "  metricImage - The location of a BMP file that is used to create a graph. \n" ;

       if (args.length != 7) {
           System.out.println(usage);
           System.exit(1);
       }

        Region region = Region.US_EAST_1;
        String myDate = args[0];
        String costDateWeek = args[1];
        String dashboardName = args[2];
        String dashboardJson = args[3];
        String dashboardAdd = args[4];
        String settings = args[5];
        String metricImage = args[6];


        Double dataPoint = Double.parseDouble("10.0");
        Scanner sc = new Scanner(System.in);
        CloudWatchClient cw = CloudWatchClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println(DASHES);
        System.out.println("Welcome to the Amazon CloudWatch example scenario.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. List at least five available unique namespaces from Amazon CloudWatch. Select one from the list.");
        ArrayList<String> list = listNameSpaces(cw);
        for (int z=0; z<5; z++) {
            int index = z+1;
            System.out.println("    " +index +". " +list.get(z));
        }

        String selectedNamespace = "";
        String selectedMetrics = "";
        int num = Integer.parseInt(sc.nextLine());
        if (1 <= num && num <= 5){
            selectedNamespace = list.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+selectedNamespace);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. List available metrics within the selected namespace and select one from the list.");
        ArrayList<String> metList = listMets(cw, selectedNamespace);
        for (int z=0; z<5; z++) {
            int index = z+1;
            System.out.println("    " +index +". " +metList.get(z));
        }
        num = Integer.parseInt(sc.nextLine());
        if (1 <= num && num <= 5){
            selectedMetrics = metList.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+selectedMetrics);
        Dimension myDimension = getSpecificMet( cw, selectedNamespace);
        System.out.println(DASHES);

        System.out.println(DASHES);
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
        num = Integer.parseInt(sc.nextLine());
        if (1 <= num && num <= 5){
            metricOption = statTypes.get(num-1);
        } else {
            System.out.println("You did not select a valid option.");
            System.exit(1);
        }
        System.out.println("You selected "+metricOption);
        getAndDisplayMetricStatistics(cw, selectedNamespace, selectedMetrics, metricOption, myDate, myDimension);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Get CloudWatch estimated billing for the last week.");
        getMetricStatistics(cw, costDateWeek);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Create a new CloudWatch dashboard with metrics.");
        createDashboardWithMetrics(cw, dashboardName, dashboardJson);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. List dashboards using a paginator.");
        listDashboards(cw);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Create a new custom metric by adding data to it.");
        createNewCustomMetric(cw, dataPoint);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Add an additional metric to the dashboard.");
        addMetricToDashboard(cw, dashboardAdd, dashboardName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Create an alarm for the custom metric.");
        String alarmName = createAlarm(cw, settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Describe ten current alarms.");
        describeAlarms(cw);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("11. Get current data for new custom metric.");
        getCustomMetricData(cw,settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("12. Push data into the custom metric to trigger the alarm.");
        addMetricDataForAlarm(cw, settings) ;
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("13. Check the alarm state using the action DescribeAlarmsForMetric.");
        checkForMetricAlarm(cw, settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("14. Get alarm history for the new alarm.");
        getAlarmHistory(cw, settings, myDate);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("15. Add an anomaly detector for the custom metric.");
        addAnomalyDetector(cw, settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("16. Describe current anomaly detectors.");
        describeAnomalyDetectors(cw, settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("17. Get a metric image for the custom metric.");
        getAndOpenMetricImage(cw, metricImage);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("18. Clean up the Amazon CloudWatch resources.");
        deleteDashboard(cw, dashboardName);
        deleteCWAlarm(cw, alarmName);
        deleteAnomalyDetector(cw, settings);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("The Amazon CloudWatch example scenario is complete.");
        System.out.println(DASHES);
        cw.close();
    }

    // snippet-start:[cloudwatch.java2.scenario.del.anomalydetector.main]
    public static void deleteAnomalyDetector(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();

            SingleMetricAnomalyDetector singleMetricAnomalyDetector = SingleMetricAnomalyDetector.builder()
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .stat("Maximum")
                .build();

            DeleteAnomalyDetectorRequest request = DeleteAnomalyDetectorRequest.builder()
                .singleMetricAnomalyDetector(singleMetricAnomalyDetector)
                .build();

            cw.deleteAnomalyDetector(request);
            System.out.println("Successfully deleted the Anomaly Detector.");

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.del.anomalydetector.main]

    // snippet-start:[cloudwatch.java2.scenario.del.alarm.main]
    public static void deleteCWAlarm(CloudWatchClient cw, String alarmName) {
        try {
            DeleteAlarmsRequest request = DeleteAlarmsRequest.builder()
                .alarmNames(alarmName)
                .build();

            cw.deleteAlarms(request);
            System.out.println("Successfully deleted alarm " +alarmName);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.del.alarm.main]

    // snippet-start:[cloudwatch.java2.scenario.del.dashboard.main]
    public static void deleteDashboard(CloudWatchClient cw, String dashboardName) {
        try {
            DeleteDashboardsRequest dashboardsRequest = DeleteDashboardsRequest.builder()
                .dashboardNames(dashboardName)
                .build();
            cw.deleteDashboards(dashboardsRequest);
            System.out.println(dashboardName + " was successfully deleted.");

        } catch (CloudWatchException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.del.dashboard.main]

    // snippet-start:[cloudwatch.java2.scenario.get.metric.image.main]
    public static void getAndOpenMetricImage(CloudWatchClient cw, String fileName) {
        System.out.println("Getting Image data for custom metric.");
        try {
              String myJSON = "{\n" +
                "  \"title\": \"Example Metric Graph\",\n" +
                "  \"view\": \"timeSeries\",\n" +
                "  \"stacked \": false,\n" +
                "  \"period\": 10,\n" +
                "  \"width\": 1400,\n" +
                "  \"height\": 600,\n" +
                "  \"metrics\": [\n" +
                "    [\n" +
                "      \"AWS/Billing\",\n" +
                "      \"EstimatedCharges\",\n" +
                "      \"Currency\",\n" +
                "      \"USD\"\n" +
                "    ]\n" +
                "  ]\n" +
                "}";

            GetMetricWidgetImageRequest imageRequest = GetMetricWidgetImageRequest.builder()
                .metricWidget(myJSON)
                .build();

            GetMetricWidgetImageResponse response = cw.getMetricWidgetImage(imageRequest);
            SdkBytes sdkBytes = response.metricWidgetImage();
            byte[] bytes = sdkBytes.asByteArray();
            File outputFile = new File(fileName);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(bytes);
            }

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.get.metric.image.main]

    // snippet-start:[cloudwatch.java2.scenario.describe.anomalydetector.main]
    public static void describeAnomalyDetectors(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();
            DescribeAnomalyDetectorsRequest detectorsRequest = DescribeAnomalyDetectorsRequest.builder()
                .maxResults(10)
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .build();

            DescribeAnomalyDetectorsResponse response = cw.describeAnomalyDetectors(detectorsRequest) ;
            List<AnomalyDetector> anomalyDetectorList = response.anomalyDetectors();
            for (AnomalyDetector detector: anomalyDetectorList) {
                System.out.println("Metric name: "+detector.singleMetricAnomalyDetector().metricName());
                System.out.println("State: "+detector.stateValue());
            }

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.describe.anomalydetector.main]

    // snippet-start:[cloudwatch.java2.scenario.add.anomalydetector.main]
    public static void addAnomalyDetector(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();

            SingleMetricAnomalyDetector singleMetricAnomalyDetector = SingleMetricAnomalyDetector.builder()
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .stat("Maximum")
                .build();

            PutAnomalyDetectorRequest anomalyDetectorRequest = PutAnomalyDetectorRequest.builder()
                .singleMetricAnomalyDetector(singleMetricAnomalyDetector)
                .build();

            cw.putAnomalyDetector(anomalyDetectorRequest);
            System.out.println("Added anomaly detector for metric "+customMetricName+".");

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.add.anomalydetector.main]

    // snippet-start:[cloudwatch.java2.scenario.get.alarm.history.main]
    public static void getAlarmHistory(CloudWatchClient cw, String fileName, String date) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String alarmName = rootNode.findValue("exampleAlarmName").asText();

            Instant start = Instant.parse(date);
            Instant endDate = Instant.now();
            DescribeAlarmHistoryRequest historyRequest = DescribeAlarmHistoryRequest.builder()
                .startDate(start)
                .endDate(endDate)
                .alarmName(alarmName)
                .historyItemType(HistoryItemType.ACTION)
                .build();

            DescribeAlarmHistoryResponse response = cw.describeAlarmHistory(historyRequest);
            List<AlarmHistoryItem>historyItems = response.alarmHistoryItems();
            if (historyItems.isEmpty()) {
                System.out.println("No alarm history data found for "+alarmName +".");
            } else {
                for (AlarmHistoryItem item: historyItems) {
                    System.out.println("History summary: "+item.historySummary());
                    System.out.println("Time stamp: "+item.timestamp());
                }
            }

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.get.alarm.history.main]

    // snippet-start:[cloudwatch.java2.scenario.check.met.alarm.main]
    public static void checkForMetricAlarm(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();
            boolean hasAlarm = false;
            int retries = 10;

            DescribeAlarmsForMetricRequest metricRequest = DescribeAlarmsForMetricRequest.builder()
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .build();

            while (!hasAlarm && retries > 0) {
                DescribeAlarmsForMetricResponse response = cw.describeAlarmsForMetric(metricRequest);
                hasAlarm = response.hasMetricAlarms();
                retries--;
                Thread.sleep(20000);
                System.out.println(".");
            }
            if (!hasAlarm)
                System.out.println("No Alarm state found for "+ customMetricName +" after 10 retries.");
            else
               System.out.println("Alarm state found for "+ customMetricName +".");

        } catch (CloudWatchException | IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.check.met.alarm.main]

    // snippet-start:[cloudwatch.java2.scenario.add.met.alarm.main]
    public static void addMetricDataForAlarm(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();

            // Set an Instant object.
            String time = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
            Instant instant = Instant.parse(time);

            MetricDatum datum = MetricDatum.builder()
                .metricName(customMetricName)
                .unit(StandardUnit.NONE)
                .value(1001.00)
                .timestamp(instant)
                .build();

            MetricDatum datum2 = MetricDatum.builder()
                .metricName(customMetricName)
                .unit(StandardUnit.NONE)
                .value(1002.00)
                .timestamp(instant)
                .build();

            List<MetricDatum> metricDataList = new ArrayList<>();
            metricDataList.add(datum);
            metricDataList.add(datum2);

            PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace(customMetricNamespace)
                .metricData(metricDataList)
                .build();

            cw.putMetricData(request);
            System.out.println("Added metric values for for metric " +customMetricName);

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.add.met.alarm.main]

    // snippet-start:[cloudwatch.java2.scenario.get.met.data.main]
    public static void getCustomMetricData(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();

            // Set the date.
            Instant nowDate = Instant.now();

            long hours = 1;
            long minutes = 30;
            Instant date2 = nowDate.plus(hours, ChronoUnit.HOURS).plus(minutes,
                ChronoUnit.MINUTES);

            Metric met = Metric.builder()
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .build();

            MetricStat metStat = MetricStat.builder()
                .stat("Maximum")
                .period(1)
                .metric(met)
                .build();

            MetricDataQuery dataQUery = MetricDataQuery.builder()
                .metricStat(metStat)
                .id("foo2")
                .returnData(true)
                .build();

            List<MetricDataQuery> dq = new ArrayList<>();
            dq.add(dataQUery);

            GetMetricDataRequest getMetReq = GetMetricDataRequest.builder()
                .maxDatapoints(10)
                .scanBy(ScanBy.TIMESTAMP_DESCENDING)
                .startTime(nowDate)
                .endTime(date2)
                .metricDataQueries(dq)
                .build();

            GetMetricDataResponse response = cw.getMetricData(getMetReq);
            List<MetricDataResult> data = response.metricDataResults();
            for (MetricDataResult item : data) {
                System.out.println("The label is " + item.label());
                System.out.println("The status code is " + item.statusCode().toString());
            }

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.get.met.data.main]

    // snippet-start:[cloudwatch.java2.scenario.describe.alarm.main]
    public static void describeAlarms(CloudWatchClient cw) {
        try {
            List<AlarmType> typeList = new ArrayList<>();
            typeList.add(AlarmType.METRIC_ALARM);

            DescribeAlarmsRequest alarmsRequest = DescribeAlarmsRequest.builder()
                .alarmTypes(typeList)
                .maxRecords(10)
                .build();

            DescribeAlarmsResponse response = cw.describeAlarms(alarmsRequest);
            List<MetricAlarm> alarmList = response.metricAlarms();
            for (MetricAlarm alarm: alarmList) {
                System.out.println("Alarm name: " + alarm.alarmName());
                System.out.println("Alarm description: " + alarm.alarmDescription());
            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.describe.alarm.main]

    // snippet-start:[cloudwatch.java2.scenario.create.alarm.main]
    public static String createAlarm(CloudWatchClient cw, String fileName) {
        try {
            // Read values from the JSON file.
            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            String customMetricNamespace = rootNode.findValue("customMetricNamespace").asText();
            String customMetricName = rootNode.findValue("customMetricName").asText();
            String alarmName = rootNode.findValue("exampleAlarmName").asText();
            String emailTopic = rootNode.findValue("emailTopic").asText();
            String accountId = rootNode.findValue("accountId").asText();
            String region = rootNode.findValue("region").asText();

            // Create a List for alarm actions.
            List<String> alarmActions = new ArrayList<>();
            alarmActions.add("arn:aws:sns:"+region+":"+accountId+":"+emailTopic);
            PutMetricAlarmRequest alarmRequest = PutMetricAlarmRequest.builder()
                .alarmActions(alarmActions)
                .alarmDescription("Example metric alarm")
                .alarmName(alarmName)
                .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                .threshold(100.00)
                .metricName(customMetricName)
                .namespace(customMetricNamespace)
                .evaluationPeriods(1)
                .period(10)
                .statistic("Maximum")
                .datapointsToAlarm(1)
                .treatMissingData("ignore")
                .build();

            cw.putMetricAlarm(alarmRequest);
            System.out.println(alarmName +" was successfully created!");
            return alarmName;

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[cloudwatch.java2.scenario.create.alarm.main]

    // snippet-start:[cloudwatch.java2.scenario.add.metric.dashboard.main]
    public static void addMetricToDashboard(CloudWatchClient cw, String fileName, String dashboardName) {
        try {
            PutDashboardRequest dashboardRequest = PutDashboardRequest.builder()
                .dashboardName(dashboardName)
                .dashboardBody(readFileAsString(fileName))
                .build();

            cw.putDashboard(dashboardRequest);
            System.out.println(dashboardName +" was successfully updated.");

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.add.metric.dashboard.main]

    // snippet-start:[cloudwatch.java2.scenario.create.metric.main]
    public static void createNewCustomMetric(CloudWatchClient cw, Double dataPoint) {
        try {
            Dimension dimension = Dimension.builder()
                .name("UNIQUE_PAGES")
                .value("URLS")
                .build();

            // Set an Instant object.
            String time = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
            Instant instant = Instant.parse(time);

            MetricDatum datum = MetricDatum.builder()
                .metricName("PAGES_VISITED")
                .unit(StandardUnit.NONE)
                .value(dataPoint)
                .timestamp(instant)
                .dimensions(dimension)
                .build();

            PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace("SITE/TRAFFIC")
                .metricData(datum)
                .build();

            cw.putMetricData(request);
            System.out.println("Added metric values for for metric PAGES_VISITED");

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.create.metric.main]

    // snippet-start:[cloudwatch.java2.scenario.list.dashboard.main]
    public static void listDashboards(CloudWatchClient cw) {
        try {
            ListDashboardsIterable listRes = cw.listDashboardsPaginator();
            listRes.stream()
                .flatMap(r -> r.dashboardEntries().stream())
                .forEach(entry ->{
                    System.out.println("Dashboard name is: " + entry.dashboardName());
                    System.out.println("Dashboard ARN is: " + entry.dashboardArn());
                });

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.list.dashboard.main]

    // snippet-start:[cloudwatch.java2.scenario.create.dashboard.main]
    public static void createDashboardWithMetrics(CloudWatchClient cw, String dashboardName, String fileName) {
        try {
            PutDashboardRequest dashboardRequest = PutDashboardRequest.builder()
                .dashboardName(dashboardName)
                .dashboardBody(readFileAsString(fileName))
                .build();

            PutDashboardResponse response = cw.putDashboard(dashboardRequest);
            System.out.println(dashboardName +" was successfully created.");
            List<DashboardValidationMessage> messages = response.dashboardValidationMessages();
            if (messages.isEmpty()) {
                System.out.println("There are no messages in the new Dashboard");
            } else {
                for (DashboardValidationMessage message : messages) {
                    System.out.println("Message is: " + message.message());
                }
            }

        } catch (CloudWatchException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.create.dashboard.main]

    public static String readFileAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    // snippet-start:[cloudwatch.java2.scenario.get.metrics.main]
    public static void getMetricStatistics(CloudWatchClient cw, String costDateWeek) {
        try {
            Instant start = Instant.parse(costDateWeek);
            Instant endDate = Instant.now();
            Dimension dimension = Dimension.builder()
                .name("Currency")
                .value("USD")
                .build();

            List<Dimension> dimensionList = new ArrayList<>();
            dimensionList.add(dimension);
            GetMetricStatisticsRequest statisticsRequest = GetMetricStatisticsRequest.builder()
                .metricName("EstimatedCharges")
                .namespace("AWS/Billing")
                .dimensions(dimensionList)
                .statistics(Statistic.MAXIMUM)
                .startTime(start)
                .endTime(endDate)
                .period(86400)
                .build();

            GetMetricStatisticsResponse response = cw.getMetricStatistics(statisticsRequest);
            List<Datapoint> data = response.datapoints();
            if (!data.isEmpty()) {
                for (Datapoint datapoint: data) {
                    System.out.println("Timestamp: " + datapoint.timestamp() + " Maximum value: " + datapoint.maximum());
                }
            } else {
                System.out.println("The returned data list is empty");
            }

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.get.metrics.main]

    // snippet-start:[cloudwatch.java2.scenario.display.metrics.main]
    public static void getAndDisplayMetricStatistics( CloudWatchClient cw, String nameSpace, String metVal, String metricOption, String date, Dimension myDimension) {
        try {
            Instant start = Instant.parse(date);
            Instant endDate = Instant.now();

            GetMetricStatisticsRequest statisticsRequest = GetMetricStatisticsRequest.builder()
                .endTime(endDate)
                .startTime(start)
                .dimensions(myDimension)
                .metricName(metVal)
                .namespace(nameSpace)
                .period(86400)
                .statistics(Statistic.fromValue(metricOption))
                .build();

            GetMetricStatisticsResponse response = cw.getMetricStatistics(statisticsRequest);
            List<Datapoint> data = response.datapoints();
            if (!data.isEmpty()) {
                for (Datapoint datapoint: data) {
                    System.out.println("Timestamp: " + datapoint.timestamp() + " Maximum value: " + datapoint.maximum());
                }
            } else {
                System.out.println("The returned data list is empty");
            }

        } catch (CloudWatchException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.scenario.display.metrics.main]

    public static Dimension getSpecificMet( CloudWatchClient cw, String namespace) {
        try {
            ListMetricsRequest request = ListMetricsRequest.builder()
                .namespace(namespace)
                .build();

            ListMetricsResponse response = cw.listMetrics(request);
            List<Metric> myList = response.metrics();
            Metric metric = myList.get(0);
            return metric.dimensions().get(0);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    // snippet-start:[cloudwatch.java2.scenario.list.metrics.main]
    public static ArrayList<String> listMets( CloudWatchClient cw, String namespace) {
        try {
            ArrayList<String> metList = new ArrayList<>();
            ListMetricsRequest request = ListMetricsRequest.builder()
                .namespace(namespace)
                .build();

            ListMetricsIterable listRes = cw.listMetricsPaginator(request);
            listRes.stream()
                .flatMap(r -> r.metrics().stream())
                .forEach(metrics -> metList.add(metrics.metricName()));

            return metList;

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[cloudwatch.java2.scenario.list.metrics.main]

    // snippet-start:[cloudwatch.java2.scenario.list.namespaces.main]
   public static ArrayList<String> listNameSpaces(CloudWatchClient cw) {
       try {
           ArrayList<String> nameSpaceList = new ArrayList<>();
           ListMetricsRequest request = ListMetricsRequest.builder()
               .build();

           ListMetricsIterable listRes = cw.listMetricsPaginator(request);
           listRes.stream()
               .flatMap(r -> r.metrics().stream())
               .forEach(metrics -> {
                   String data = metrics.namespace();
                   if(!nameSpaceList.contains(data)) {
                       nameSpaceList.add(data);
                   }
               }) ;

           return nameSpaceList;
           } catch (CloudWatchException e) {
               System.err.println(e.awsErrorDetails().errorMessage());
               System.exit(1);
           }
       return null;
   }
    // snippet-end:[cloudwatch.java2.scenario.list.namespaces.main]
}
// snippet-end:[cloudwatch.java2.scenario.main]