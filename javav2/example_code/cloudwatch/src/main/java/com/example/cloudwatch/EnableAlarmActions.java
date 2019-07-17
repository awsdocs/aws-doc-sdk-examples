//snippet-sourcedescription:[EnableAlarmActions.java demonstrates how to enable actions on a CloudWatch alarm.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java2.enable_alarm_actions.complete]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[cloudwatch.java2.enable_alarm_actions.import]
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.EnableAlarmActionsRequest;
import software.amazon.awssdk.services.cloudwatch.model.EnableAlarmActionsResponse;
// snippet-end:[cloudwatch.java2.enable_alarm_actions.import]

/**
 * Enables actions on a CloudWatch alarm
 */
public class EnableAlarmActions {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an alarm name\n" +
            "Ex: EnableAlarmActions <alarm-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarm = args[0];

        // snippet-start:[cloudwatch.java2.enable_alarm_actions.main]
        CloudWatchClient cw =
        		CloudWatchClient.builder().build();

        EnableAlarmActionsRequest request = EnableAlarmActionsRequest.builder()
            .alarmNames(alarm).build();

        EnableAlarmActionsResponse response = cw.enableAlarmActions(request);
        // snippet-end:[cloudwatch.java2.enable_alarm_actions.main]

        System.out.printf(
            "Successfully enabled actions on alarm %s", alarm);
    }
}
// snippet-end:[cloudwatch.java2.enable_alarm_actions.complete]