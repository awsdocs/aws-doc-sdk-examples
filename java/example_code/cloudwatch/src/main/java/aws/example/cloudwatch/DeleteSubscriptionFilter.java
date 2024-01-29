// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DeleteSubscriptionFilterRequest;
import com.amazonaws.services.logs.model.DeleteSubscriptionFilterResult;

/**
 * Deletes a CloudWatch Logs subscription filter.
 */
public class DeleteSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a filter name and log group name\n" +
                "Ex: DeleteSubscriptionFilter <filter-name> <log-group-name>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String log_group = args[1];

        final AWSLogs logs = AWSLogsClientBuilder.defaultClient();

        DeleteSubscriptionFilterRequest request = new DeleteSubscriptionFilterRequest()
                .withFilterName(filter)
                .withLogGroupName(log_group);

        DeleteSubscriptionFilterResult response = logs.deleteSubscriptionFilter(request);

        System.out.printf(
                "Successfully deleted CloudWatch logs subscription filter %s",
                filter);
    }
}
