//snippet-sourcedescription:[DisableAlarmActions.java demonstrates how to disable actions on a CloudWatch alarm.]
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
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.disable_alarm_actions.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.DisableAlarmActionsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DisableAlarmActionsResponse;
// snippet-end:[cloudwatch.java2.disable_alarm_actions.import]

/**
 * Disables actions on a CloudWatch alarm
 */
public class DisableAlarmActions {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply an alarm name\n" +
                        "Ex: DisableAlarmActions <alarm-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarmName = args[0];

        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .build();

        disableActions(cw, alarmName) ;
    }

    // snippet-start:[cloudwatch.java2.disable_alarm_actions.main]
    public static void disableActions(CloudWatchClient cw, String alarmName) {

        try {
             DisableAlarmActionsRequest request = DisableAlarmActionsRequest.builder()
                .alarmNames(alarmName).build();

            DisableAlarmActionsResponse response = cw.disableAlarmActions(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf(
                "Successfully disabled actions on alarm %s", alarmName);
    }
    // snippet-end:[cloudwatch.java2.disable_alarm_actions.main]
}
