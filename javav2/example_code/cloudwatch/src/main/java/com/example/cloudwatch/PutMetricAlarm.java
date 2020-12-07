//snippet-sourcedescription:[PutMetricAlarm.java demonstrates how to create a new Amazon CloudWatch alarm based on CPU utilization for an instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_metric_alarm.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.cloudwatch.model.ComparisonOperator;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
// snippet-end:[cloudwatch.java2.put_metric_alarm.import]

public class PutMetricAlarm {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  PutMetricAlarm <alarmName> <instanceId> \n\n" +
                "Where:\n" +
                "  alarmName - an alarm name to use.\n" +
                "  instanceId - an instance Id value .\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarmName = args[0];
        String instanceId = args[1];

        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .build();

        putMetricAlarm(cw, alarmName, instanceId) ;
        cw.close();
    }

    // snippet-start:[cloudwatch.java2.put_metric_alarm.main]
    public static void putMetricAlarm(CloudWatchClient cw, String alarmName, String instanceId) {

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

            cw.putMetricAlarm(request);
            System.out.printf(
                    "Successfully created alarm with name %s", alarmName);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
       // snippet-end:[cloudwatch.java2.put_metric_alarm.main]
    }
}
