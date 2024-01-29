// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsResult;

/**
 * Deletes a CloudWatch alarm
 */
public class DeleteAlarm {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an alarm name\n" +
                "Ex: DeleteAlarm <alarm-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarm_name = args[0];

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

        DeleteAlarmsRequest request = new DeleteAlarmsRequest()
                .withAlarmNames(alarm_name);

        DeleteAlarmsResult response = cw.deleteAlarms(request);

        System.out.printf("Successfully deleted alarm %s", alarm_name);
    }
}
