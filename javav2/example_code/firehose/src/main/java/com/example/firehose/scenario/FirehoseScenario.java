// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.firehose.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.PutRecordBatchRequest;
import software.amazon.awssdk.services.firehose.model.PutRecordBatchResponse;
import software.amazon.awssdk.services.firehose.model.PutRecordRequest;
import software.amazon.awssdk.services.firehose.model.Record;
import software.amazon.awssdk.regions.Region;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FirehoseScenario {
    private static FirehoseClient firehoseClient;
    private static CloudWatchClient cloudWatchClient;
    private static String deliveryStreamName;

    public static void main(String[] args) {
        firehoseClient = FirehoseClient.builder().region(Region.US_EAST_1).build();
        cloudWatchClient = CloudWatchClient.builder().region(Region.US_EAST_1).build();

        // Replace with your region and delivery stream name
        deliveryStreamName = "stream35";

        /*
           See the Readme in the scenario folder for information about the sample_records.json file.
           After you create this file, place in your project's resources folder After you perform these tasks,
           the sample data file can be used in this scenario.
         */
        String jsonContent = readJsonFile("sample_records.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> sampleData = objectMapper.readValue(
                jsonContent,
                new TypeReference<>() {}
            );

            // Process individual records
            System.out.println("Processing individual records...");
            sampleData.subList(0, 100).forEach(record -> {
                try {
                    putRecord(record);
                } catch (Exception e) {
                    System.out.println("Put record failed: " + e.getMessage());
                }
            });

            monitorMetrics();

            // Process batch records.
            System.out.println("Processing batch records...");
            putRecordBatch(sampleData.subList(100, 200), 50);
            monitorMetrics();
        } catch (Exception e) {
            System.out.println("Error processing records: " + e.getMessage());
        } finally {
            closeClients();
        }

        System.out.println("This concludes the AWS Firehose scenario...");
    }

    /**
     * Puts a record to the specified Amazon Kinesis Data Firehose delivery stream.
     *
     * @param record a {@link java.util.Map} containing the data to be sent to the Firehose delivery stream
     * @throws RuntimeException if an exception occurs while sending the record to the Firehose delivery stream
     */
    public static void putRecord(Map<String, Object> record) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRecord = objectMapper.writeValueAsString(record);
            ByteBuffer data = ByteBuffer.wrap(jsonRecord.getBytes());

            Record firehoseRecord = Record.builder()
                .data(SdkBytes.fromByteBuffer(data))
                .build();

            PutRecordRequest putRecordRequest = PutRecordRequest.builder()
                .deliveryStreamName(deliveryStreamName)
                .record(firehoseRecord)
                .build();

            firehoseClient.putRecord(putRecordRequest);
            System.out.println("Record sent successfully: " + jsonRecord);
        } catch (Exception e) {
            System.out.println("Failed to send record. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Puts a batch of records to the Amazon Kinesis Firehose delivery stream.
     *
     * @param records     a list of maps representing the records to be sent to the delivery stream
     * @param batchSize   the maximum number of records to include in each batch
     *
     * @throws RuntimeException if there is an error converting the records to Firehose records or sending the batch to the delivery stream
     */
    public static void putRecordBatch(List<Map<String, Object>> records, int batchSize) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (int i = 0; i < records.size(); i += batchSize) {
                List<Map<String, Object>> batch = records.subList(i, Math.min(i + batchSize, records.size()));
                List<Record> batchRecords = batch.stream()
                    .map(record -> {
                        try {
                            String jsonRecord = objectMapper.writeValueAsString(record);
                            return Record.builder()
                                .data(SdkBytes.fromByteArray(jsonRecord.getBytes()))
                                .build();
                        } catch (Exception e) {
                            throw new RuntimeException("Error converting record to Firehose Record", e);
                        }
                    }).collect(Collectors.toList());

                PutRecordBatchRequest request = PutRecordBatchRequest.builder()
                    .deliveryStreamName(deliveryStreamName)
                    .records(batchRecords)
                    .build();

                PutRecordBatchResponse response = firehoseClient.putRecordBatch(request);

                if (response.failedPutCount() > 0) {
                    System.out.println("Failed to send " + response.failedPutCount() + " records in batch of " + batchRecords.size());
                    response.requestResponses().stream()
                        .filter(r -> r.errorCode() != null)
                        .forEach(r -> System.out.println("Error: " + r.errorMessage()));
                } else {
                    System.out.println("Successfully sent batch of " + batchRecords.size() + " records");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to send batch records. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Monitors the specified metrics over the last 10 minutes (600 seconds).
     * <p>
     * This method retrieves the values of the following metrics:
     * <ul>
     *     <li>IncomingBytes</li>
     *     <li>IncomingRecords</li>
     *     <li>FailedPutCount</li>
     * </ul>
     * and logs any exceptions that occur during the monitoring process.
     */
    public static void monitorMetrics() {
        try {
            Instant endTime = Instant.now();
            Instant startTime = endTime.minusSeconds(600);

            List<String> metrics = List.of("IncomingBytes", "IncomingRecords", "FailedPutCount");
            metrics.forEach(metric -> monitorMetric(metric, startTime, endTime));
        } catch (Exception e) {
            System.out.println("Failed to monitor metrics. Error: " + e.getMessage());
        }
    }

    /**
     * Monitors a specific metric and logs the total sum of the metric values.
     *
     * @param metricName the name of the metric to monitor
     * @param startTime the start time of the metric collection period
     * @param endTime the end time of the metric collection period
     */
    private static void monitorMetric(String metricName, Instant startTime, Instant endTime) {
        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
            .namespace("AWS/Firehose")
            .metricName(metricName)
            .dimensions(Dimension.builder().name("DeliveryStreamName").value(deliveryStreamName).build())
            .startTime(startTime)
            .endTime(endTime)
            .period(60)
            .statistics(Statistic.SUM)
            .build();

        GetMetricStatisticsResponse response = cloudWatchClient.getMetricStatistics(request);
        double totalSum = response.datapoints().stream()
            .mapToDouble(Datapoint::sum)
            .sum();

        System.out.println(metricName + ": " + Math.round(totalSum));
    }

    public static String readJsonFile(String fileName) {
        ClassLoader classLoader = FirehoseScenario.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException("Error reading resource file: " + fileName, e);
        }
    }

    private static void closeClients() {
        try {
            if (firehoseClient != null) {
                firehoseClient.close();
            }
            if (cloudWatchClient != null) {
                cloudWatchClient.close();
            }
        } catch (Exception e) {
            System.out.println("Failed to close clients: " + e.getMessage());
        }
    }
}
