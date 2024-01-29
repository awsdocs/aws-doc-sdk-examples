// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.EnableAlarmActionsRequest;
import com.amazonaws.services.cloudwatch.model.EnableAlarmActionsResult;

/**
 * Enables actions on a CloudWatch alarm
 */
public class EnableAlarmActions {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an alarm name\n" +
                "Ex: EnableAlarmActions <alarm-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarm = args[0];

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

        EnableAlarmActionsRequest request = new EnableAlarmActionsRequest()
                .withAlarmNames(alarm);

        EnableAlarmActionsResult response = cw.enableAlarmActions(request);

        System.out.printf(
                "Successfully enabled actions on alarm %s", alarm);
    }
}
