//snippet-sourcedescription:[EnableAlarmActions.java demonstrates how to enable actions on a CloudWatch alarm.]
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

// snippet-start:[cloudwatch.java2.enable_alarm_actions.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.EnableAlarmActionsRequest;
import software.amazon.awssdk.services.cloudwatch.model.EnableAlarmActionsResponse;
// snippet-end:[cloudwatch.java2.enable_alarm_actions.import]


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class EnableAlarmActions {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "  EnableAlarmActions <alarmName>\n\n" +
                "Where:\n" +
                "  alarmName - an alarm name to enable (for example, MyAlarm).\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarm = args[0];
        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .build();

        enableActions(cw, alarm) ;
        cw.close();
    }

    // snippet-start:[cloudwatch.java2.enable_alarm_actions.main]
    public static void enableActions(CloudWatchClient cw, String alarm) {

        try {
            EnableAlarmActionsRequest request = EnableAlarmActionsRequest.builder()
                .alarmNames(alarm).build();

           cw.enableAlarmActions(request);
            System.out.printf(
                    "Successfully enabled actions on alarm %s", alarm);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        // snippet-end:[cloudwatch.java2.enable_alarm_actions.main]
   }
}

