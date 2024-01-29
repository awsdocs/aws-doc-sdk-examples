// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.DisableAlarmActionsRequest;
import com.amazonaws.services.cloudwatch.model.DisableAlarmActionsResult;

/**
 * Disables actions on a CloudWatch alarm
 */
public class DisableAlarmActions {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an alarm name\n" +
                "Ex: DisableAlarmActions <alarm-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarmName = args[0];

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

        DisableAlarmActionsRequest request = new DisableAlarmActionsRequest()
                .withAlarmNames(alarmName);

        DisableAlarmActionsResult response = cw.disableAlarmActions(request);

        System.out.printf(
                "Successfully disabled actions on alarm %s", alarmName);
    }
}
