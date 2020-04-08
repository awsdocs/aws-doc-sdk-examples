//snippet-sourcedescription:[GetMetricData.java demonstrates how to get metric data.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

// snippet-start:[cloudwatch.java2.get_metric_data.import]
package com.example.cloudwatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
// snippet-end:[cloudwatch.java2.get_metric_data.import]

/**
 * Puts a sample metric data point
 */
public class GetMetricData {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .build();

        getMetData(cw) ;
    }

    // snippet-start:[cloudwatch.java2.get_metric_alarm.main]
    public static void getMetData( CloudWatchClient cw) {

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
                    .id("foo2")
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
        // snippet-end:[cloudwatch.java2.get_metric_alarm.main]
    }
}
