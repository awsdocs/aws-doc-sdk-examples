//snippet-sourcedescription:[DisableAlarmActions.java demonstrates how to disable actions on an Amazon CloudWatch alarm.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.disable_alarm_actions.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.DisableAlarmActionsRequest;
// snippet-end:[cloudwatch.java2.disable_alarm_actions.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DisableAlarmActions {

    public static void main(String[] args) {

       final String usage = "\n" +
                "Usage:\n" +
                "  <alarmName>\n\n" +
                "Where:\n" +
                "  alarmName - An alarm name to disable (for example, MyAlarm).\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String alarmName = args[0];
        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        disableActions(cw, alarmName) ;
        cw.close();
    }

    // snippet-start:[cloudwatch.java2.disable_alarm_actions.main]
    public static void disableActions(CloudWatchClient cw, String alarmName) {

        try {
             DisableAlarmActionsRequest request = DisableAlarmActionsRequest.builder()
                     .alarmNames(alarmName)
                     .build();

            cw.disableAlarmActions(request);
            System.out.printf(
                    "Successfully disabled actions on alarm %s", alarmName);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.disable_alarm_actions.main]
}
