//snippet-sourcedescription:[PutMetricAlarm.java demonstrates how to create a new CloudWatch alarm based on CPU utilization for an instance.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[13/02/2020]
//snippet-sourceauthor:[scmacdon]

/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[cloudwatch.java2.put_metric_alarm.complete]
// snippet-start:[cloudwatch.java2.put_metric_alarm.import]
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.cloudwatch.model.ComparisonOperator;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmResponse;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
// snippet-end:[cloudwatch.java2.put_metric_alarm.import]
/**
 * Creates a new CloudWatch alarm based on CPU utilization for an instance
 */
public class PutMetricAlarm {
    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply an alarm name and instance id\n" +
                        "Ex: DeleteAlarm <alarm-name> <instance-id>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarmName = args[0];
        String instanceId = args[1];

        // snippet-start:[cloudwatch.java2.put_metric_alarm.main]
        CloudWatchClient cw = CloudWatchClient.builder().build();

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

       System.out.printf(
                "Successfully created alarm with name %s", alarmName);
        // snippet-end:[cloudwatch.java2.put_metric_alarm.main]
    }
}
// snippet-end:[cloudwatch.java2.put_metric_alarm.complete]
