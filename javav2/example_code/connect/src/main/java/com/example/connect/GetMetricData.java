//snippet-sourcedescription:[GetMetricData.java demonstrates how to get historical metric data from the specified Amazon Connect instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.Channel;
import software.amazon.awssdk.services.connect.model.Comparison;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.Filters;
import software.amazon.awssdk.services.connect.model.GetMetricDataRequest;
import software.amazon.awssdk.services.connect.model.GetMetricDataResponse;
import software.amazon.awssdk.services.connect.model.HistoricalMetric;
import software.amazon.awssdk.services.connect.model.HistoricalMetricData;
import software.amazon.awssdk.services.connect.model.HistoricalMetricName;
import software.amazon.awssdk.services.connect.model.HistoricalMetricResult;
import software.amazon.awssdk.services.connect.model.Statistic;
import software.amazon.awssdk.services.connect.model.Threshold;
import software.amazon.awssdk.services.connect.model.Unit;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * For more information about this operation, see the following operation topic:
 *
 * https://docs.aws.amazon.com/connect/latest/APIReference/API_GetMetricData.html
 */
public class GetMetricData {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The identifier of the Amazon Connect instance.\n\n" +
            "   queueId - The identifier of the queue.\n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        String queueId = args[1];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        getHistoricalMetrics (connectClient, instanceId, queueId);
    }

    // snippet-start:[connect.java2.historical.main]
    public static void getHistoricalMetrics (ConnectClient connectClient, String instanceId, String queueId) {
        try {
            // Define the metrics to retrieve.
            Threshold threshold = Threshold.builder()
                .comparison(Comparison.LT)
                .thresholdValue(10.0)
                .build();

            HistoricalMetric contactMetric = HistoricalMetric.builder()
                .name(HistoricalMetricName.CONTACTS_HANDLED)
                .statistic(Statistic.SUM)
                .threshold(threshold)
                .unit(Unit.COUNT)
                .build();

            Filters filter = Filters.builder()
                .channels(Channel.VOICE)
                .queues(queueId)
                .build();

            String stringDate = "09:05:00 AM, Tue 01/03/2023"; // Add a new date value.
            String pattern = "hh:mm:ss a, EEE M/d/uuuu";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
            LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
            Instant startInstant = localDateTime.toInstant(ZoneOffset.UTC);

            String stringDate2 = "10:05:00 AM, Tue 01/03/2023"; // Add a new date value.
            DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern(pattern, Locale.US);
            LocalDateTime localDateTime2 = LocalDateTime.parse(stringDate2, dateTimeFormatter2);
            Instant endInstant = localDateTime2.toInstant(ZoneOffset.UTC);

            GetMetricDataRequest dataRequest = GetMetricDataRequest.builder()
                .instanceId(instanceId)
                .endTime(endInstant)
                .startTime(startInstant)
                .filters(filter)
                .maxResults(10)
                .historicalMetrics(contactMetric)
                 .build();

            GetMetricDataResponse response = connectClient.getMetricData(dataRequest);
            List<HistoricalMetricResult> resultList = response.metricResults();
            for (HistoricalMetricResult result: resultList) {
                List<HistoricalMetricData> colls = result.collections();
                   for (HistoricalMetricData data: colls) {
                       System.out.println("The statistic name is "+ data.metric().statistic().name());
                   }
            }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.historical.main]
}
