//snippet-sourcedescription:[PutMetricData.java demonstrates how to put a sample metric data point for a metric defined for a CloudWatch alarm.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.cloudwatch;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

/**
 * Puts a sample metric data point
 */
public class PutMetricData {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a data point:\n" +
            "Ex: PutMetricData <data_point>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        Double data_point = Double.parseDouble(args[0]);

        CloudWatchClient cw =
        		CloudWatchClient.builder().build();

        Dimension dimension = Dimension.builder()
            .name("UNIQUE_PAGES")
            .value("URLS").build();

        MetricDatum datum = MetricDatum.builder()
            .metricName("PAGES_VISITED")
            .unit(StandardUnit.NONE)
            .value(data_point)
            .dimensions(dimension).build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
            .namespace("SITE/TRAFFIC")
            .metricData(datum).build();

        PutMetricDataResponse response = cw.putMetricData(request);

        System.out.printf("Successfully put data point %f", data_point);
    }
}
